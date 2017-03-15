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

import org.scalatest.{Matchers, FlatSpec}

import com.outworkers.morpheus.column.KnownTypeLimits
import com.outworkers.morpheus.tables.StringsTable

class StringColumnSerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple CHAR definition to an SQL query without a limit set" in {
    StringsTable.charColumn.qb.queryString shouldEqual s"`charColumn` CHAR(${KnownTypeLimits.charLimit})"
  }

  it should "serialise a simple CHAR definition to an SQL query with a limit set" in {
    StringsTable.charLimited.qb.queryString shouldEqual s"`charLimited` CHAR(100)"
  }

  it should "serialise a simple VARCHAR definition to an SQL query without a limit set" in {
    StringsTable.varChar.qb.queryString shouldEqual s"`varChar` VARCHAR(${KnownTypeLimits.varcharLimit})"
  }

  it should "serialise a simple VARCHAR definition to an SQL query with a limit set" in {
    StringsTable.varCharLimited.qb.queryString shouldEqual "`varCharLimited` VARCHAR(100)"
  }

  it should "serialise a TINYTEXT column definition to the correct SQL type" in {
    StringsTable.tinyText.qb.queryString shouldEqual "`tinyText` TINYTEXT"
  }

  it should "serialise a MEDIUMTEXT column definition to the correct SQL type" in {
    StringsTable.mediumText.qb.queryString shouldEqual "`mediumText` MEDIUMTEXT"
  }

  it should "serialise a LONGTEXT column definition to the correct SQL type" in {
    StringsTable.longText.qb.queryString shouldEqual "`longText` LONGTEXT"
  }

  it should "serialise a TEXT column definition to the correct SQL type" in {
    StringsTable.textColumn.qb.queryString shouldEqual "`textColumn` TEXT"
  }
}
