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
package com.outworkers.morpheus.dsl

import org.scalatest.{FlatSpec, Matchers}

class TableTest extends FlatSpec with Matchers {

  it should "correctly initialise table columns via reflection and force greedy object initialisation of Table object members" in {
    BasicTable.columns.size shouldEqual 2
  }

  it should "correctly extract the name of a table directly from the Scala object name" in {
    BasicTable.tableName shouldEqual "BasicTable"
  }

  it should "correctly extract the name of the columns inside a table" in {
    BasicTable.count.name shouldEqual "count"
    BasicTable.name.name shouldEqual "name"
  }

}
