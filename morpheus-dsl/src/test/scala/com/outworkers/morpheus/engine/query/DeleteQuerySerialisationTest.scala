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

import com.outworkers.morpheus.dsl._
import com.outworkers.morpheus.sql._
import org.scalatest.{FlatSpec, Matchers}

class DeleteQuerySerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple DELETE query" in {
    BasicTable.delete.queryString shouldEqual "DELETE FROM `BasicTable`;"
  }

  it should  "serialise a simple DELETE where query" in {
    BasicTable.delete
      .where(_.name eqs "test").queryString shouldEqual "DELETE FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise an DELETE query with an < operator" in {
    BasicTable.delete
      .where(_.count < 5).queryString shouldEqual "DELETE FROM `BasicTable` WHERE count < 5;"
  }

  it should "serialise an DELETE query with an lt operator" in {
    BasicTable.delete
      .where(_.count lt 5)
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE count < 5;"
  }

  it should "serialise an DELETE query with an <= operator" in {
    BasicTable.delete
      .where(_.count <= 5)
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE count <= 5;"
  }

  it should "serialise an DELETE query with an lte operator" in {
    BasicTable.delete
      .where(_.count lte 5)
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE count <= 5;"
  }

  it should "serialise an DELETE query with a gt operator" in {
    BasicTable.delete
      .where(_.count gt 5)
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE count > 5;"
  }

  it should "serialise an DELETE query with a > operator" in {
    BasicTable.delete
      .where(_.count > 5)
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE count > 5;"
  }

  it should "serialise an DELETE query with a gte operator" in {
    BasicTable.delete
      .where(_.count gte 5).queryString shouldEqual "DELETE FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise an DELETE query with a >= operator" in {
    BasicTable.delete
      .where(_.count >= 5).queryString shouldEqual "DELETE FROM `BasicTable` WHERE count >= 5;"
  }

  it should  "not allow specifying the WHERE part before the SET in an DELETE query" in {
    """BasicTable.delete.where(_.name eqs 5).queryString""" shouldNot compile
  }

  it should "serialise a multiple assignments query with a single where clause" in {
    BasicTable.delete
      .where(_.name eqs "test")
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a multiple assignments query with a multiple where clause" in {
    BasicTable.delete
      .where(_.name eqs "test")
      .and(_.count eqs 10)
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE name = 'test' AND count = 10;"
  }


  it should  "serialise a simple DELETE where-and query" in {
    BasicTable.delete
      .where(_.name eqs "test")
      .and(_.count eqs 5).queryString shouldEqual "DELETE FROM `BasicTable` WHERE name = 'test' AND count = 5;"
  }

  it should "serialise a conditional DELETE clause with an OR operator" in {
    BasicTable.delete
      .where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE name = 'test' AND (count = 5 OR name = 'test');"
  }

  it should "serialise a conditional DELETE clause with an a double WHERE-OR operator" in {
    BasicTable.delete
      .where(t => { (t.count eqs 15) or (t.name eqs "test5") })
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE FROM `BasicTable` WHERE (count = 15 OR name = 'test5') AND (count = 5 OR name = 'test');"
  }
}
