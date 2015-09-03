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


class UpdateQuerySerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple UPDATE query" in {
    BasicTable.update.queryString shouldEqual "UPDATE `BasicTable`;"
  }

  it should  "serialise a simple UPDATE where query" in {
    BasicTable.update.set(_.count setTo 10).where(_.name eqs "test")
      .queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE name = 'test';"
  }

  it should "serialise an UPDATE query with an < operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count < 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count < 5;"
  }

  it should "serialise an UPDATE query with an lt operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count lt 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count < 5;"
  }

  it should "serialise an UPDATE query with an <= operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count <= 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count <= 5;"
  }

  it should "serialise an UPDATE query with an lte operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count lte 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count <= 5;"
  }

  it should "serialise an UPDATE query with a gt operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count gt 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count > 5;"
  }

  it should "serialise an UPDATE query with a > operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count > 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count > 5;"
  }

  it should "serialise an UPDATE query with a gte operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count gte 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count >= 5;"
  }

  it should "serialise an UPDATE query with a >= operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count >= 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count >= 5;"
  }

  it should  "not allow specifying the WHERE part before the SET in an UPDATE query" in {
    """BasicTable.update.where(_.name eqs 5).queryString""" shouldNot compile
  }

  it should "serialise a simple assignments query" in {
    BasicTable.update.set(_.name setTo "test").queryString shouldEqual "UPDATE `BasicTable` SET name = 'test';"
  }

  it should "serialise a simple assignments query with a single where clause" in {
    BasicTable.update
      .set(_.name setTo "test")
      .where(_.count eqs 15)
      .queryString shouldEqual "UPDATE `BasicTable` SET name = 'test' WHERE count = 15;"
  }

  it should "serialise a multiple assignments query with a single where clause" in {
    BasicTable.update
      .set(_.name setTo "test2")
      .andSet(_.count setTo 15)
      .where(_.name eqs "test")
      .queryString shouldEqual "UPDATE `BasicTable` SET name = 'test2', count = 15 WHERE name = 'test';"
  }

  it should "serialise a multiple assignments query with a multiple where clause" in {
    BasicTable.update
      .set(_.name setTo "test2")
      .andSet(_.count setTo 15)
      .where(_.name eqs "test")
      .and(_.count eqs 10)
      .queryString shouldEqual "UPDATE `BasicTable` SET name = 'test2', count = 15 WHERE name = 'test' AND count = 10;"
  }


  it should  "serialise a simple UPDATE where-and query" in {
    BasicTable.update
      .set(_.count setTo 10)
      .where(_.name eqs "test")
      .and(_.count eqs 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE name = 'test' AND count = 5;"
  }

  it should "serialise a conditional UPDATE clause with an OR operator" in {
    BasicTable.update
      .set(_.count setTo 10)
      .where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE name = 'test' AND (count = 5 OR name = 'test');"
  }

  it should "serialise a conditional UPDATE clause with an a double WHERE-OR operator" in {
    BasicTable.update
      .set(_.count setTo 10)
      .where(t => { (t.count eqs 15) or (t.name eqs "test5") })
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE (count = 15 OR name = 'test5') AND (count = 5 OR name = 'test');"
  }

}
