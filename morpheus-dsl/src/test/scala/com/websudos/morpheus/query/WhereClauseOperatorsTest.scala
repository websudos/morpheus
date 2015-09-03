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
import com.websudos.morpheus.sql._

import com.websudos.morpheus.dsl.BasicTable

class WhereClauseOperatorsTest extends FlatSpec with Matchers {
  it should "serialise a SELECT clause with a BETWEEN - AND operator sequence" in {
    BasicTable.select.where(_.count between 5 and 10).queryString shouldEqual "SELECT * FROM `BasicTable` WHERE count BETWEEN 5 AND 10;"
  }

  it should "serialise a SELECT clause with a BETWEEN - AND operator sequence inside an OR sequence" in {
    BasicTable.select
      .where(t => { (t.count between 5 and 10) or (t.count gte 5) })
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE (count BETWEEN 5 AND 10 OR count >= 5);"
  }

  it should "serialise a SELECT clause with a NOT BETWEEN - AND operator sequence" in {
    BasicTable.select.where(_.count notBetween 5 and 10)
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE count NOT BETWEEN 5 AND 10;"
  }

  it should "serialise a SELECT clause with a NOT BETWEEN - AND operator sequence inside an OR sequence" in {
    BasicTable.select
      .where(t => { (t.count notBetween 5 and 10) or (t.count gte 5) })
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE (count NOT BETWEEN 5 AND 10 OR count >= 5);"
  }
}
