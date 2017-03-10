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
package com.outworkers.morpheus.engine.query

import com.outworkers.morpheus.dsl.BasicTable
import org.scalatest.{FlatSpec, Matchers}
import com.outworkers.morpheus.sql._
import com.outworkers.morpheus.tables.IndexTable

class InFlightOperatorsTest extends FlatSpec with Matchers {

  it should "serialise an inFlight usage of a EXISTS operator" in {
    exists(BasicTable.select.where(_.count eqs 10))
      .clause.queryString shouldEqual "EXISTS (SELECT * FROM `BasicTable` WHERE count = 10)"
  }

  it should "serialise a nested EXISTS sub-query" in {
    BasicTable.select
      .where(exists(BasicTable.select.where(_.count eqs 10)))
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE EXISTS (SELECT * FROM `BasicTable` WHERE count = 10);"
  }


  it should "serialise an inFlight usage of a NOT EXISTS operator" in {
    notExists(BasicTable.select.where(_.count eqs 10)).clause
      .queryString shouldEqual "NOT EXISTS (SELECT * FROM `BasicTable` WHERE count = 10)"

  }

  it should "serialise a nested NOT EXISTS sub-query" in {
    BasicTable.select
      .where(notExists(BasicTable.select.where(_.count eqs 10)))
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE NOT EXISTS (SELECT * FROM `BasicTable` WHERE count = 10);"
  }

  it should "serialise a three nested alternation of EXISTS/NOT EXISTS sub-queries" in {

    val qb = BasicTable.select
      .where(notExists(BasicTable.select.where(exists(IndexTable.select.where(_.id eqs 10)))))
      .queryString

    qb shouldEqual "SELECT * FROM `BasicTable` WHERE NOT EXISTS (SELECT * FROM `BasicTable` WHERE EXISTS (SELECT * FROM `IndexTable` WHERE id = 10));"
  }

  it should "serialise a CONCAT clause to the appropiate select query" in {
    rootSelectQueryToSelectQuery(BasicTable.select(_ => concat("A", "B", "C", "D"))).queryString shouldEqual "SELECT CONCAT ('A', 'B', 'C', 'D') FROM " +
      "`BasicTable`;"
  }

  it should "serialise an INTERVAL operator clause to a select query" in {
    BasicTable.select(_ => interval(5, 5, 10)).queryString shouldEqual "SELECT INTERVAL (5, 5, 10) FROM `BasicTable`;"
  }
}
