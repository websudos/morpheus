/*
 * Copyright 2015 websudos ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.websudos.morpheus.mysql.db

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.{Matchers, BeforeAndAfterAll, FlatSpec}

import com.websudos.morpheus.mysql._
import com.websudos.morpheus.mysql.tables.{BasicRecord, BasicTable}
import com.websudos.util.testing._

class InsertQueryDBTest extends FlatSpec with MySQLSuite with BeforeAndAfterAll with Matchers {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Await.ready(BasicTable.create.ifNotExists.engine(InnoDB).future(), 3.seconds)
  }

  it should "store a record in the database and retrieve it by id" in {
    val sample = gen[BasicRecord]

    val chain = for {
      store <- BasicTable.insert.value(_.name, sample.name).value(_.count, sample.count).future()
      get <- BasicTable.select.where(_.name eqs sample.name).one()
    } yield get

    chain.successful {
      res => {
        res.isDefined shouldEqual true
        res.get shouldEqual sample
      }
    }
  }
}
