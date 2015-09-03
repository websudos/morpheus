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

import com.websudos.morpheus.column.KnownTypeLimits
import com.websudos.morpheus.tables.StringsTable

class StringColumnSerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple CHAR definition to an SQL query without a limit set" in {
    StringsTable.charColumn.qb.queryString shouldEqual s"charColumn CHAR(${KnownTypeLimits.charLimit})"
  }

  it should "serialise a simple CHAR definition to an SQL query with a limit set" in {
    StringsTable.charLimited.qb.queryString shouldEqual s"charLimited CHAR(100)"
  }

  it should "serialise a simple VARCHAR definition to an SQL query without a limit set" in {
    StringsTable.varChar.qb.queryString shouldEqual s"varChar VARCHAR(${KnownTypeLimits.varcharLimit})"
  }

  it should "serialise a simple VARCHAR definition to an SQL query with a limit set" in {
    StringsTable.varCharLimited.qb.queryString shouldEqual "varCharLimited VARCHAR(100)"
  }

  it should "serialise a TINYTEXT column definition to the correct SQL type" in {
    StringsTable.tinyText.qb.queryString shouldEqual "tinyText TINYTEXT"
  }

  it should "serialise a MEDIUMTEXT column definition to the correct SQL type" in {
    StringsTable.mediumText.qb.queryString shouldEqual "mediumText MEDIUMTEXT"
  }

  it should "serialise a LONGTEXT column definition to the correct SQL type" in {
    StringsTable.longText.qb.queryString shouldEqual "longText LONGTEXT"
  }

  it should "serialise a TEXT column definition to the correct SQL type" in {
    StringsTable.textColumn.qb.queryString shouldEqual "textColumn TEXT"
  }
}
