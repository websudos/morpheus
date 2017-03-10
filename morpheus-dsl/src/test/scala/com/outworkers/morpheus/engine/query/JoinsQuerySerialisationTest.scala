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

import org.scalatest.{FlatSpec, Matchers}

import com.outworkers.morpheus.sql._
import com.outworkers.morpheus.tables.{IndexTable, KeysTable}

class JoinsQuerySerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple LEFT JOIN query" in {
    val qb = KeysTable
      .select
      .where(_.id eqs 10)
      .leftJoin(IndexTable)
      .on(_.foreignKey eqs IndexTable.value)
      .queryString

    qb shouldEqual "SELECT * FROM `KeysTable` WHERE id = 10 LEFT JOIN `IndexTable` ON KeysTable.foreignKey = IndexTable.value;"
  }

  it should "serialise a simple INNER JOIN query" in {
    val qb = KeysTable
      .select
      .where(_.id eqs 10)
      .innerJoin(IndexTable)
      .on(_.foreignKey eqs IndexTable.value)
      .queryString

    qb shouldEqual "SELECT * FROM `KeysTable` WHERE id = 10 INNER JOIN `IndexTable` ON KeysTable.foreignKey = IndexTable.value;"
  }

}
