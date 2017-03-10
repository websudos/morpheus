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
package com.outworkers.morpheus.column

import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.dsl.BaseTable
import org.scalatest.{Matchers, FlatSpec}

class TestColumn extends AbstractColumn[Int] {
  override def qb: SQLBuiltQuery = SQLBuiltQuery("integer")

  override def toQueryString(v: Int): String = ???

  override def sqlType: String = ???

  override def table: BaseTable[_, _, _] = ???
}

class AbstractColumnTest extends FlatSpec with Matchers {

  it should "resolve column name from the name of implementing class" in {
    val column = new TestColumn

    column.name shouldEqual "TestColumn"
  }

}
