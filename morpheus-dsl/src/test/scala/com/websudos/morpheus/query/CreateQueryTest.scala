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

package com.websudos.morpheus.query

import org.scalatest.{Matchers, FlatSpec}

import com.websudos.morpheus.dsl.BasicTable
import com.websudos.morpheus.sql._

class CreateQueryTest extends FlatSpec with Matchers {

  it should "serialise a simple CREATE query" in {
    BasicTable.create.queryString shouldEqual "CREATE TABLE `BasicTable`;"
  }

  it should "serialise a simple CREATE query with an IF NOT EXISTS clause" in {
    BasicTable.create.ifNotExists.queryString shouldEqual "CREATE TABLE IF NOT EXISTS `BasicTable`;"
  }

  it should "serialise a CREATE query with a TEMPORARY clause" in {
    BasicTable.create.temporary.queryString shouldEqual "CREATE TEMPORARY TABLE `BasicTable`;"
  }

  it should "serialise a CREATE create query with a TEMPORARY clause" in {
    BasicTable.create.temporary.queryString shouldEqual "CREATE TEMPORARY TABLE `BasicTable`;"
  }

  ignore should "serialise a CREATE create query with a TEMPORARY clause and an IF NOT EXISTS clause" in {
    BasicTable.create.temporary.ifNotExists.queryString shouldEqual "CREATE TEMPORARY TABLE IF NOT EXISTS `BasicTable`;"
  }

  it should "serialise a complete table definition when an engine is specified" in {
    BasicTable.create.engine(InnoDB).queryString shouldEqual "CREATE TABLE `BasicTable` (name TEXT, count LONG) ENGINE InnoDB;"
  }

  it should "serialise a complete table definition with an IF NOT EXSITS clause when an engine is specified" in {
    BasicTable.create.ifNotExists.engine(InnoDB)
      .queryString shouldEqual "CREATE TABLE IF NOT EXISTS `BasicTable` (name TEXT, count LONG) ENGINE InnoDB;"
  }


}
