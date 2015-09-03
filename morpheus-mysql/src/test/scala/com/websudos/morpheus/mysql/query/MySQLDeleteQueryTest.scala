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
package com.websudos.morpheus.mysql.query

import org.scalatest.{FlatSpec, Matchers}

import com.websudos.morpheus.mysql._
import com.websudos.morpheus.mysql.tables.BasicTable

class MySQLDeleteQueryTest extends FlatSpec with Matchers {

  it should "serialise a simple DELETE LOW_PRIORITY query" in {
    BasicTable.delete
      .lowPriority.queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable;"
  }

  it should  "serialise a simple DELETE LOW_PRIORITY where query" in {
    BasicTable.delete
      .lowPriority
      .where(_.name eqs "test").queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE name = 'test';"
  }

  it should "serialise an DELETE LOW_PRIORITY query with an < operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count < 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count < 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with an lt operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count lt 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count < 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with an <= operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count <= 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count <= 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with an lte operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count lte 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count <= 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with a gt operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count gt 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count > 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with a > operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count > 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count > 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with a gte operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count gte 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count >= 5;"
  }

  it should "serialise an DELETE LOW_PRIORITY query with a >= operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.count >= 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count >= 5;"
  }

  it should "serialise a simple DELETE LOW_PRIORITY assignments query" in {
    BasicTable.delete
      .lowPriority
      .queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable;"
  }

  it should "serialise a simple DELETE LOW_PRIORITY assignments query with a single where clause" in {
    BasicTable.delete
      .lowPriority
      .where(_.count eqs 15).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE count = 15;"
  }

  it should "serialise a multiple assignments DELETE LOW_PRIORITY  query with a single where clause" in {
    BasicTable.delete
      .lowPriority
      .where(_.name eqs "test")
      .queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE name = 'test';"
  }

  it should  "serialise a simple DELETE LOW_PRIORITY where-and query" in {
    BasicTable.delete
      .lowPriority
      .where(_.name eqs "test")
      .and(_.count eqs 5).queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE name = 'test' AND count = 5;"
  }

  it should "serialise a conditional DELETE LOW_PRIORITY clause with an OR operator" in {
    BasicTable.delete
      .lowPriority
      .where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE name = 'test' AND (count = 5 OR name = 'test');"
  }

  it should "serialise a conditional DELETE LOW_PRIORITY clause with an a double WHERE-OR operator" in {
    BasicTable.delete
      .lowPriority
      .where(t => { (t.count eqs 15) or (t.name eqs "test5") })
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE LOW_PRIORITY FROM BasicTable WHERE (count = 15 OR name = 'test5') AND (count = 5 OR name = 'test');"
  }

  it should "serialise a simple DELETE IGNORE query" in {
    BasicTable.delete
      .ignore.queryString shouldEqual "DELETE IGNORE FROM BasicTable;"
  }

  it should  "serialise a simple DELETE IGNORE where query" in {
    BasicTable.delete
      .ignore
      .where(_.name eqs "test").queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE name = 'test';"
  }

  it should "serialise an DELETE IGNORE query with an < operator" in {
    BasicTable.delete
      .ignore
      .where(_.count < 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count < 5;"
  }

  it should "serialise an DELETE IGNORE query with an lt operator" in {
    BasicTable.delete
      .ignore
      .where(_.count lt 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count < 5;"
  }

  it should "serialise an DELETE IGNORE query with an <= operator" in {
    BasicTable.delete
      .ignore
      .where(_.count <= 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count <= 5;"
  }

  it should "serialise an DELETE IGNORE query with an lte operator" in {
    BasicTable.delete
      .ignore
      .where(_.count lte 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count <= 5;"
  }

  it should "serialise an DELETE IGNORE query with a gt operator" in {
    BasicTable.delete
      .ignore
      .where(_.count gt 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count > 5;"
  }

  it should "serialise an DELETE IGNORE query with a > operator" in {
    BasicTable.delete
      .ignore
      .where(_.count > 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count > 5;"
  }

  it should "serialise an DELETE IGNORE query with a gte operator" in {
    BasicTable.delete
      .ignore
      .where(_.count gte 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count >= 5;"
  }

  it should "serialise an DELETE IGNORE query with a >= operator" in {
    BasicTable.delete
      .ignore
      .where(_.count >= 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count >= 5;"
  }

  it should "serialise a simple DELETE IGNORE assignments query" in {
    BasicTable.delete
      .ignore
      .queryString shouldEqual "DELETE IGNORE FROM BasicTable;"
  }

  it should "serialise a simple DELETE IGNORE assignments query with a single where clause" in {
    BasicTable.delete
      .ignore
      .where(_.count eqs 15).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE count = 15;"
  }

  it should "serialise a multiple assignments DELETE IGNORE  query with a single where clause" in {
    BasicTable.delete
      .ignore
      .where(_.name eqs "test")
      .queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE name = 'test';"
  }

  it should  "serialise a simple DELETE IGNORE where-and query" in {
    BasicTable.delete
      .ignore
      .where(_.name eqs "test")
      .and(_.count eqs 5).queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE name = 'test' AND count = 5;"
  }

  it should "serialise a conditional DELETE IGNORE clause with an OR operator" in {
    BasicTable.delete
      .ignore
      .where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE name = 'test' AND (count = 5 OR name = 'test');"
  }

  it should "serialise a conditional DELETE IGNORE clause with an a double WHERE-OR operator" in {
    BasicTable.delete
      .ignore
      .where(t => { (t.count eqs 15) or (t.name eqs "test5") })
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "DELETE IGNORE FROM BasicTable WHERE (count = 15 OR name = 'test5') AND (count = 5 OR name = 'test');"
  }
}
