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
package com.outworkers.morpheus.mysql.query

import com.outworkers.morpheus.mysql.tables.BasicTable
import com.outworkers.util.testing._
import com.outworkers.morpheus.mysql.dsl._
import org.scalatest.{FlatSpec, Matchers}

class InsertQueryTest extends FlatSpec with Matchers {

  it should "serialise an INSERT IGNORE INTO query to the correct query" in {
    BasicTable.insert.ignore.queryString shouldEqual "INSERT IGNORE INTO BasicTable;"
  }

  it should "serialise an INSERT HIGH_PRIORITY INTO query to the correct query" in {
    BasicTable.insert.highPriority.queryString shouldEqual "INSERT HIGH_PRIORITY INTO BasicTable;"
  }

  it should "serialise an INSERT LOW_PRIORITY INTO query to the correct query" in {
    BasicTable.insert.lowPriority.queryString shouldEqual "INSERT LOW_PRIORITY INTO BasicTable;"
  }

  it should "serialise an INSERT DELAYED INTO query to the correct query" in {
    BasicTable.insert.delayed.queryString shouldEqual "INSERT DELAYED INTO BasicTable;"
  }

  it should "serialise an INSERT value query with a single value clause" in {
    BasicTable.insert.value(_.name, "test").queryString shouldEqual "INSERT INTO `BasicTable` (name) VALUES('test');"
  }

  it should "serialise an INSERT value query with a multiple value clauses" in {
    val num = gen[Int]
    BasicTable.insert.value(_.name, "test").value(_.count, num)
      .queryString shouldEqual s"INSERT INTO `BasicTable` (name, count) VALUES('test', $num);"
  }

}
