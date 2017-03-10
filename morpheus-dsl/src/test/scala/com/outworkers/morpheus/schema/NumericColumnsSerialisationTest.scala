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
package com.outworkers.morpheus.schema

import org.scalatest.{FlatSpec, Matchers}

import com.outworkers.morpheus.tables.NumericsTable

class NumericColumnsSerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple TinyIntColumn definition to an SQL query without a limit set" in {
    NumericsTable.tinyInt.qb.queryString shouldEqual "tinyInt TINYINT"
  }

  it should "serialise a limited TinyIntColumn definition to an SQL query with a limit set" in {
    NumericsTable.tinyIntLimited.qb.queryString shouldEqual "tinyIntLimited TINYINT(100)"
  }

  it should "serialise a simple SmallIntColumn definition to an SQL query without a limit set" in {
    NumericsTable.smallInt.qb.queryString shouldEqual "smallInt SMALLINT"
  }

  it should "serialise a limited SmallIntColumn definition to an SQL query with a limit set" in {
    NumericsTable.smallIntLimited.qb.queryString shouldEqual "smallIntLimited SMALLINT(100)"
  }

  it should "serialise a simple MediumIntColumn definition to an SQL query without a limit set" in {
    NumericsTable.mediumInt.qb.queryString shouldEqual "mediumInt MEDIUMINT"
  }

  it should "serialise a limited MediumIntColumn definition to an SQL query with a limit set" in {
    NumericsTable.mediumIntLimited.qb.queryString shouldEqual "mediumIntLimited MEDIUMINT(100)"
  }

  it should "serialise a simple IntColumn definition to an SQL query without a limit set" in {
    NumericsTable.int.qb.queryString shouldEqual "int INT"
  }

  it should "serialise a limited IntColumn definition to an SQL query with a limit set" in {
    NumericsTable.intLimited.qb.queryString shouldEqual "intLimited INT(100)"
  }


  it should "serialise a year column definition" in {
    NumericsTable.yearCol.qb.queryString shouldEqual "yearCol YEAR"
  }

}
