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
import com.websudos.morpheus.tables.IndexTable

class InFlightOperatorsTest extends FlatSpec with Matchers {

  it should "serialise an inFlight usage of a EXISTS operator" in {
    exists(BasicTable.select.where(_.count eqs 10))
      .clause.queryString shouldEqual "EXISTS (SELECT * FROM `BasicTable` WHERE count = 10)"
  }

  it should "serialise a nested EXISTS sub-query" in {
    BasicTable.select
      .where(exists(BasicTable.select.where(_.count eqs 10)))
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE EXISTS (SELECT * FROM `BasicTable` WHERE count = 10);"
  }


  it should "serialise an inFlight usage of a NOT EXISTS operator" in {
    notExists(BasicTable.select.where(_.count eqs 10)).clause
      .queryString shouldEqual "NOT EXISTS (SELECT * FROM `BasicTable` WHERE count = 10)"

  }

  it should "serialise a nested NOT EXISTS sub-query" in {
    BasicTable.select
      .where(notExists(BasicTable.select.where(_.count eqs 10)))
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE NOT EXISTS (SELECT * FROM `BasicTable` WHERE count = 10);"
  }

  it should "serialise a three nested alternation of EXISTS/NOT EXISTS sub-queries" in {

    val qb = BasicTable.select
      .where(notExists(BasicTable.select.where(exists(IndexTable.select.where(_.id eqs 10)))))
      .queryString

    qb shouldEqual "SELECT * FROM `BasicTable` WHERE NOT EXISTS (SELECT * FROM `BasicTable` WHERE EXISTS (SELECT * FROM `IndexTable` WHERE id = 10));"
  }

  it should "serialise a CONCAT clause to the appropiate select query" in {
    rootSelectQueryToSelectQuery(BasicTable.select(_ => concat("A", "B", "C", "D"))).queryString shouldEqual "SELECT CONCAT ('A', 'B', 'C', 'D') FROM " +
      "`BasicTable`;"
  }

  it should "serialise an INTERVAL operator clause to a select query" in {
    BasicTable.select(_ => interval(5, 5, 10)).queryString shouldEqual "SELECT INTERVAL (5, 5, 10) FROM `BasicTable`;"
  }
}
