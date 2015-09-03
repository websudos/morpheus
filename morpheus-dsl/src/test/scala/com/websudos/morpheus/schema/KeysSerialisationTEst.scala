/*
 * Copyright 2013-2015 Websudos, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Explicit consent must be obtained from the copyright owner, Websudos Limited before any redistribution is made.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.websudos.morpheus.schema

import org.scalatest.{Matchers, FlatSpec}

import com.websudos.morpheus.tables.KeysTable

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
