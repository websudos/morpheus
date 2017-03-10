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

import com.outworkers.morpheus.tables.ZeroFillTable

class ZeroFillColumnsSerialisationTest extends FlatSpec with Matchers {

  it should "serialise a ZEROFILL UNSIGNED NOT NULL column" in {
    ZeroFillTable.tinyInt.qb.queryString shouldEqual "tinyInt TINYINT ZEROFILL UNSIGNED NOT NULL"
  }

  it should "serialise a ZEROFILL NOT NULL column" in {
    ZeroFillTable.tinyIntLimited.qb.queryString shouldEqual "tinyIntLimited TINYINT(5) ZEROFILL NOT NULL"
  }

  it should "serialise a ZEROFILL UNSIGNED column" in {
    ZeroFillTable.smallInt.qb.queryString shouldEqual "smallInt SMALLINT ZEROFILL UNSIGNED"
  }

}
