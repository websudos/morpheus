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

import com.outworkers.morpheus.sql._
import com.outworkers.morpheus.dsl._
import org.scalatest.{FlatSpec, Matchers}

class InsertQuerySerialisationTest extends FlatSpec with Matchers {

  it should "serialise an INSERT INTO query to the correct query and convert using an implicit" in {
    BasicTable.insert.queryString shouldEqual "INSERT INTO `BasicTable`;"
  }

  it should "serialise an INSERT INTO query to the correct query" in {
    BasicTable.insert.into.queryString shouldEqual "INSERT INTO `BasicTable`;"
  }

  it should "serialise an INSERT query with a single value defined" in {
    BasicTable.insert
      .value(_.count, 5L)
      .queryString shouldEqual "INSERT INTO `BasicTable` (count) VALUES(5);"
  }

  it should "serialise an INSERT query with multiple values defined" in {
    BasicTable.insert
      .value(_.count, 5L)
      .value(_.name, "test")
      .queryString shouldEqual "INSERT INTO `BasicTable` (count, name) VALUES(5, 'test');"
  }
}
