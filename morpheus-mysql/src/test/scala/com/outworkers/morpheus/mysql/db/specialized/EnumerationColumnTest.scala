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
package com.outworkers.morpheus.mysql.db.specialized

import com.outworkers.morpheus.DataType
import com.outworkers.morpheus.mysql.dsl._
import com.outworkers.morpheus.mysql.db.BaseSuite
import com.outworkers.morpheus.mysql.tables.{EnumerationRecord, EnumerationTable, TestEnumeration}
import com.outworkers.util.samplers.{Generators, Sample}
import com.outworkers.util.testing._
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class EnumerationColumnTest extends FlatSpec with BaseSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Await.result(EnumerationTable.create.ifNotExists.engine(InnoDB).future(), 5.seconds)
  }

  implicit object EnumerationRecordSampler extends Sample[EnumerationRecord] {
    override def sample: EnumerationRecord = EnumerationRecord(gen[Int], Generators.oneOf(TestEnumeration))
  }

  implicit val enumPrimitive: DataType[TestEnumeration#Value] = SQLPrimitive(TestEnumeration)

  it should "store a record with an enumeration defined inside it" in {
    val record = gen[EnumerationRecord]

    val chain = for {
      store <- EnumerationTable.store(record).future()
      get <- EnumerationTable.select.where(_.id eqs record.id).one
    } yield get

    whenReady(chain) { res =>
      res.value shouldEqual record
    }
  }
}
