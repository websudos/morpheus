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

import com.outworkers.morpheus.tables.KeysTable
import org.scalatest.{FlatSpec, Matchers}

class KeysSerialisationTest extends FlatSpec with Matchers {

  it should "serialise a PrimaryKey definition to an SQL query" in {
    KeysTable.id.qb.queryString shouldEqual "id INT PRIMARY KEY"
  }

  it should "serialise a PrimaryKey NotNull definition to an SQL query" in {
    KeysTable.notNullId.qb.queryString shouldEqual "notNullId INT PRIMARY KEY NOT NULL"
  }

  it should "serialise a PrimaryKey Autoincrement definition to an SQL query" in {
    KeysTable.autoincrementedId.qb.queryString shouldEqual "autoincrementedId INT PRIMARY KEY AUTO_INCREMENT"
  }

  it should "serialise a PrimaryKey NotNull AutoIncrement definition to an SQL query" in {
    KeysTable.indexId.qb.queryString shouldEqual "indexId INT PRIMARY KEY NOT NULL AUTO_INCREMENT"
  }


  it should "serialise a simple ForeignKey definition to an SQL query without any constraints defined by default" in {
    KeysTable.foreignKey.qb.queryString shouldEqual "FOREIGN KEY (IndexTable_id, IndexTable_value) REFERENCES IndexTable(id, value)"
  }

  it should "serialise a simple ForeignKey definition to an SQL query with an onUpdate constraint defined" in {
    KeysTable.foreignUpdateKey.qb.queryString shouldEqual "FOREIGN KEY (IndexTable_id, IndexTable_value) REFERENCES IndexTable(id, value) ON UPDATE CASCADE"
  }

  it should "serialise a simple ForeignKey definition to an SQL query with an onDelete constraint defined" in {
    KeysTable.foreignDeleteKey.qb.queryString shouldEqual "FOREIGN KEY (IndexTable_id, IndexTable_value) REFERENCES IndexTable(id, value) ON DELETE CASCADE"
  }

  it should "serialise a simple ForeignKey definition to an SQL query with both constraints defined" in {
    KeysTable.foreignFull.qb.queryString shouldEqual "FOREIGN KEY (IndexTable_id, IndexTable_value) REFERENCES IndexTable(id, " +
      "value) ON UPDATE CASCADE ON DELETE CASCADE"
  }


  it should "serialise a simple ForeignKey definition to an SQL query with both constraints defined as RESTRICT" in {
    KeysTable.foreignFullRestrict.qb.queryString shouldEqual "FOREIGN KEY (IndexTable_id, IndexTable_value) REFERENCES IndexTable(id, " +
      "value) ON UPDATE RESTRICT ON DELETE RESTRICT"
  }

  it should "serialise a simple ForeignKey definition to an SQL query with both constraints defined as RESTRICT and SET NULL" in {
    KeysTable.foreignFullRestrictSetNull.qb.queryString shouldEqual "FOREIGN KEY (IndexTable_id, IndexTable_value) REFERENCES IndexTable(id, " +
      "value) ON UPDATE RESTRICT ON DELETE SET NULL"
  }

  it should "serialise a UniqueKey definition to an SQL query" in {
    KeysTable.uniqueIndex.qb.queryString shouldEqual "uniqueIndex TEXT UNIQUE KEY"
  }

  it should "serialise a UniqueKey NotNull definition to an SQL query" in {
    KeysTable.uniqueIndex.qb.queryString shouldEqual "uniqueIndex TEXT UNIQUE KEY"
  }
}
