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
package com.outworkers.morpheus.mysql.db

import com.outworkers.morpheus.mysql.dsl._
import com.outworkers.morpheus.mysql.tables.{BasicRecord, BasicTable, PrimitiveRecord, PrimitivesTable}
import com.outworkers.util.testing._
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class InsertQueryDBTest extends FlatSpec with BaseSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Await.result(BasicTable.create.ifNotExists.engine(InnoDB).future(), 3.seconds)
    Console.println(PrimitivesTable.create.ifNotExists.engine(InnoDB).queryString)
    Await.result(PrimitivesTable.create.ifNotExists.engine(InnoDB).future(), 3.seconds)
  }

  it should "store a record in the database and retrieve it by id" in {
    val sample = gen[BasicRecord]

    val chain = for {
      store <- BasicTable.insert.value(_.name, sample.name).value(_.count, sample.count).future()
      one <- BasicTable.select.where(_.name eqs sample.name).one()
    } yield one

    whenReady(chain) { res =>
      res.value shouldEqual sample
    }
  }

  it should "insert and select a record with all the primitive types in MySQL" in {
    val sample = gen[PrimitiveRecord]

    val chain = for {
      store <- PrimitivesTable.store(sample).future()
      one <- PrimitivesTable.select.where(_.id eqs sample.id).one()
    } yield one

    whenReady(chain) { res =>
      res.value.id shouldEqual sample.id
      res.value.double shouldEqual sample.double
      res.value.float shouldEqual sample.float
      res.value.long shouldEqual sample.long
      res.value.str shouldEqual sample.str
      // res.value.datetime shouldEqual sample.datetime
      // res.value.date shouldEqual sample.date
    }
  }
}
