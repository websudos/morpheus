/*
 * Copyright 2013-2015 Websudos, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Explicit consent must be obtained from the copyright owner, Websudos Limited before any redistribution is made.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.websudos.morpheus.mysql.db

import com.twitter.conversions.time._
import com.twitter.finagle.exp.Mysql
import com.twitter.util.Await
import com.websudos.morpheus.Client
import com.websudos.morpheus.mysql.{MySQLClient, MySQLResult, MySQLRow}
import org.scalatest.{Suite, BeforeAndAfterAll, Matchers, OptionValues}
import org.scalatest.concurrent.{AsyncAssertions, PatienceConfiguration, ScalaFutures}
import org.scalatest.time.{Seconds, Span}

object MySQLConnector {

  def isRunningUnderTravis: Boolean = {
    System.getenv.containsKey("TRAVIS")
  }

  val user = if (isRunningUnderTravis) "travis" else "morpheus"
  val pwd = "morpheus23!"

  /**
   * This client is meant to connect to the Travis CI default MySQL service.
   */
  lazy val client = {
    val c = Mysql.client
      .withCredentials(user, pwd)
      .withDatabase("morpheus_test")
      .newRichClient("127.0.0.1:3306")
    Await.result(c.ping(), 2.seconds)
    c
  }
}


trait MySQLSuite extends AsyncAssertions
  with ScalaFutures
  with OptionValues
  with Matchers
  with BeforeAndAfterAll {

  this: Suite =>

  implicit lazy val client: Client[MySQLRow, MySQLResult] = new MySQLClient(MySQLConnector.client)

  implicit def patience: PatienceConfiguration.Timeout = timeout(Span(5L, Seconds))
}

// CREATE USER 'morpheus'@'localhost' IDENTIFIED BY 'morpheus23!';
// GRANT ALL PRIVILEGES ON * . * TO 'morpheus'@'localhost';
