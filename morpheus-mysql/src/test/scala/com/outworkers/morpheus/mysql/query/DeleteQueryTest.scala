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
import org.scalatest.{FlatSpec, Matchers}
import com.outworkers.morpheus.mysql.dsl._

class DeleteQueryTest extends FlatSpec with Matchers {

  it should "serialise a simple DELETE LOW_PRIORITY query" in {
    BasicTable.delete
      .lowPriority.queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable;"
  }

  it should  "serialise a simple DELETE LOW_PRIORITY where query" in {
    BasicTable.delete
      .lowPriority
      .where(_.name eqs "test").queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE name = 'test';"
  }

  it should "serialise an DELETE LOW_PRIORITY query with an < operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count < 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count < 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with an lt operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count lt 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count < 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with an <= operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count <= 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count <= 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with an lte operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count lte 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count <= 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with a gt operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count gt 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count > 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with a > operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count > 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count > 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with a gte operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count gte 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count >= 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with a >= operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count >= 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count >= 5;"
  }

  it should "serialise a simple DELETE LOW_PRIORITY assignments query" in {
    BasicTable.delete
      .lowPriority
      .queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable;"
  }

  it should "serialise a simple DELETE LOW_PRIORITY assignments query with a single where clause" in {
    BasicTable.delete
      .lowPriority
      .where(_.count eqs 15).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count = 15;"
  }

  it should "serialise a multiple assignments DELETE LOW_PRIORITY  query with a single where clause" in {
    BasicTable.delete
      .lowPriority
      .where(_.name eqs "test")
      .queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE name = 'test';"
  }

  it should  "serialise a simple DELETE LOW_PRIORITY where-and query" in {
    BasicTable.delete
      .lowPriority
      .where(_.name eqs "test")
      .and(_.count eqs 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE name = 'test' AND count = 5;"
  }

  it should "serialise a conditional DELETE LOW_PRIORITY clause with an OR operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE name = 'test' AND (count = 5 OR name = 'test');"
  }

  it should "serialise a conditional DELETE LOW_PRIORITY clause with an a double WHERE-OR operator" in {
    BasicTable.delete
      .lowPriority
      .where(t => { (t.count eqs 15) or (t.name eqs "test5") })
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE (count = 15 OR name = 'test5') AND (count = 5 OR name = 'test');"
  }

  it should "serialise a simple DELETE IGNORE query" in {
    BasicTable.delete
      .ignore.queryString shouldEqual "DELETE IGNORE FROM BasicTable;"
  }

  it should  "serialise a simple DELETE IGNORE where query" in {
    BasicTable.delete
      .ignore
      .where(_.name eqs "test").queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE name = 'test';"
  }

  it should "serialise an DELETE IGNORE query with an < operator" in {
    BasicTable.delete
      .ignore
      .where(_.count < 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count < 5;"
  }

  it should "serialise an DELETE IGNORE query with an lt operator" in {
    BasicTable.delete
      .ignore
      .where(_.count lt 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count < 5;"
  }

  it should "serialise an DELETE IGNORE query with an <= operator" in {
    BasicTable.delete
      .ignore
      .where(_.count <= 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count <= 5;"
  }

  it should "serialise an DELETE IGNORE query with an lte operator" in {
    BasicTable.delete
      .ignore
      .where(_.count lte 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count <= 5;"
  }

  it should "serialise an DELETE IGNORE query with a gt operator" in {
    BasicTable.delete
      .ignore
      .where(_.count gt 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count > 5;"
  }

  it should "serialise an DELETE IGNORE query with a > operator" in {
    BasicTable.delete
      .ignore
      .where(_.count > 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count > 5;"
  }

  it should "serialise an DELETE IGNORE query with a gte operator" in {
    BasicTable.delete
      .ignore
      .where(_.count gte 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count >= 5;"
  }

  it should "serialise an DELETE IGNORE query with a >= operator" in {
    BasicTable.delete
      .ignore
      .where(_.count >= 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count >= 5;"
  }

  it should "serialise a simple DELETE IGNORE assignments query" in {
    BasicTable.delete
      .ignore
      .queryString shouldEqual "DELETE IGNORE FROM BasicTable;"
  }

  it should "serialise a simple DELETE IGNORE assignments query with a single where clause" in {
    BasicTable.delete
      .ignore
      .where(_.count eqs 15).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count = 15;"
  }

  it should "serialise a multiple assignments DELETE IGNORE  query with a single where clause" in {
    BasicTable.delete
      .ignore
      .where(_.name eqs "test")
      .queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE name = 'test';"
  }

  it should  "serialise a simple DELETE IGNORE where-and query" in {
    BasicTable.delete
      .ignore
      .where(_.name eqs "test")
      .and(_.count eqs 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE name = 'test' AND count = 5;"
  }

  it should "serialise a conditional DELETE IGNORE clause with an OR operator" in {
    BasicTable.delete
      .ignore
      .where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE name = 'test' AND (count = 5 OR name = 'test');"
  }

  it should "serialise a conditional DELETE IGNORE clause with an a double WHERE-OR operator" in {
    BasicTable.delete
      .ignore
      .where(t => { (t.count eqs 15) or (t.name eqs "test5") })
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE (count = 15 OR name = 'test5') AND (count = 5 OR name = 'test');"
  }
}
