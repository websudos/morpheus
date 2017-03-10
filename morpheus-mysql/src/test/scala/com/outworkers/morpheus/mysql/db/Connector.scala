/*
 * Copyright 2013 - 2017 Outworkers Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.outworkers.morpheus

package mysql
package db

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import sys.process._
import com.twitter.finagle.exp.Mysql
import org.scalatest.concurrent.{PatienceConfiguration, ScalaFutures, Waiters}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, Suite}
import org.slf4j.LoggerFactory

object Connector {

  private[this] val init = new AtomicBoolean(false)

  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  def isRunningUnderTravis: Boolean = sys.env.contains("TRAVIS")

  private[this] val databaseName = "morpheus_test"
  val user = "morpheus"
  val pwd = "morpheus23!"

  /**
   * This client is meant to connect to the Travis CI default MySQL service.
   */
  lazy val client = Mysql.client
    .withCredentials(user, pwd)
    .withDatabase("morpheus_test")
    .newRichClient("127.0.0.1:3306")

  def initialise(): Unit = {
    if (!isRunningUnderTravis && init.compareAndSet(false, true)) {
      logger.info("Initialising process database")
      val procs = List(
        s"""mysql -e "CREATE DATABASE IF NOT EXISTS $databaseName;" """,
        s"""mysql -e "CREATE USER IF NOT EXISTS '$user'@'localhost' IDENTIFIED BY '$pwd';" """,
        s"""mysql -e GRANT ALL PRIVILEGES ON * . * TO '$user'@'localhost'""",
        s"""mysql -e "SET PASSWORD FOR '$user'@'localhost' = PASSWORD('$pwd')""""
      )

      Console.println(procs.mkString("\n"))

      /*procs.foreach { cmd =>
        Console.println(s"Trying to execute command $cmd")
        val proc = cmd.!

        if (proc != 0) {
          logger.error(s"Failed to initialise the database with user $user")
        } else {
          logger.info(s"Successfully initialised the local database $databaseName with user $user")
        }
      }*/

    } else {
      logger.info("Local database is already initialised.")
    }
  }
}

trait BaseSuite extends Waiters
  with ScalaFutures
  with OptionValues
  with Matchers
  with BeforeAndAfterAll {

  this: Suite =>

  implicit lazy val client = new mysql.Client(Connector.client)

  protected[this] val defaultScalaTimeoutSeconds = 10

  private[this] val defaultScalaInterval = 50L

  implicit val defaultScalaTimeout = scala.concurrent.duration.Duration(defaultScalaTimeoutSeconds, TimeUnit.SECONDS)

  private[this] val defaultTimeoutSpan = Span(defaultScalaTimeoutSeconds, Seconds)

  implicit val defaultTimeout: PatienceConfiguration.Timeout = timeout(defaultTimeoutSpan)

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  override implicit val patienceConfig = PatienceConfig(
    timeout = defaultTimeoutSpan,
    interval = Span(defaultScalaInterval, Millis)
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    Connector.initialise()
  }
}

// CREATE USER 'morpheus'@'localhost' IDENTIFIED BY 'morpheus23!';
// GRANT ALL PRIVILEGES ON * . * TO 'morpheus'@'localhost';
