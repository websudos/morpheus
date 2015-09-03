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

import org.scalatest.{FlatSpec, Matchers}

import com.websudos.morpheus.tables.NumericsTable


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


}
