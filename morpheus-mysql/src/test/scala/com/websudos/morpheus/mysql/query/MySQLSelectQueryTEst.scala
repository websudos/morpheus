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

class MySQLSelectQueryTest extends FlatSpec with Matchers {

  it should "serialise a simple SELECT DISTINCTROW query" in {
    BasicTable.select.distinctRow.queryString shouldEqual "SELECT DISTINCTROW * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT DISTINCTROW query with an WHERE clause" in {
    BasicTable.select.distinctRow.where(_.name eqs "test")
      .queryString shouldEqual "SELECT DISTINCTROW * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT DISTINCTROW query with a single column in the partial select" in {
    BasicTable.select(_.name).distinctRow.queryString shouldEqual "SELECT DISTINCTROW name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT DISTINCTROW query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).distinctRow.where(_.count >= 5)
      .queryString shouldEqual "SELECT DISTINCTROW name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).distinctRow.queryString shouldEqual "SELECT DISTINCTROW name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count)
      .distinctRow.where(_.count <= 10)
      .queryString shouldEqual "SELECT DISTINCTROW name, count FROM `BasicTable` WHERE count <= 10;"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinctRow
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT DISTINCTROW name, count FROM `BasicTable` WHERE count >= 10 AND count <= 100;"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinctRow
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")})
      .queryString shouldEqual "SELECT DISTINCTROW name, " +
      "count FROM `BasicTable` WHERE count >= 10 AND (count <= 100 OR name = 'test');"
  }

  it should "serialise a simple SELECT HIGH_PRIORITY query" in {
    BasicTable.select.highPriority.queryString shouldEqual "SELECT HIGH_PRIORITY * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT HIGH_PRIORITY query with an WHERE clause" in {
    BasicTable.select
      .highPriority
      .where(_.name eqs "test")
      .queryString shouldEqual "SELECT HIGH_PRIORITY * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with a single column in the partial select" in {
    BasicTable.select(_.name)
      .highPriority
      .queryString shouldEqual "SELECT HIGH_PRIORITY name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name)
      .highPriority
      .where(_.count >= 5)
      .queryString shouldEqual "SELECT HIGH_PRIORITY name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count)
      .highPriority
      .queryString shouldEqual "SELECT HIGH_PRIORITY name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count)
      .highPriority
      .where(_.count <= 10)
      .queryString shouldEqual "SELECT HIGH_PRIORITY name, count FROM `BasicTable` WHERE count <= 10;"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .highPriority
      .where(_.count >= 10)
      .and(_.count <= 100)
      .queryString shouldEqual "SELECT HIGH_PRIORITY name, count FROM `BasicTable` WHERE count >= 10 AND count <= 100;"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .highPriority
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT HIGH_PRIORITY name, " +
      "count FROM `BasicTable` WHERE count >= 10 AND (count <= 100 OR name = 'test');"
  }

  it should "serialise a simple SELECT STRAIGHT_JOIN query" in {
    BasicTable.select.straightJoin.queryString shouldEqual "SELECT STRAIGHT_JOIN * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT STRAIGHT_JOIN query with an WHERE clause" in {
    BasicTable.select
      .straightJoin
      .where(_.name eqs "test")
      .queryString shouldEqual "SELECT STRAIGHT_JOIN * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with a single column in the partial select" in {
    BasicTable.select(_.name)
      .straightJoin
      .queryString shouldEqual "SELECT STRAIGHT_JOIN name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name)
      .straightJoin.where(_.count >= 5)
      .queryString shouldEqual "SELECT STRAIGHT_JOIN name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).straightJoin
      .queryString shouldEqual "SELECT STRAIGHT_JOIN name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).straightJoin.where(_.count <= 10)
      .queryString shouldEqual "SELECT STRAIGHT_JOIN name, count FROM `BasicTable` WHERE count <= 10;"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .straightJoin
      .where(_.count >= 10)
      .and(_.count <= 100)
      .queryString shouldEqual "SELECT STRAIGHT_JOIN name, count FROM `BasicTable` WHERE count >= 10 AND count <= 100;"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .straightJoin
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT STRAIGHT_JOIN name, " +
      "count FROM `BasicTable` WHERE count >= 10 AND (count <= 100 OR name = 'test');"
  }

  it should "serialise a simple SELECT SQL_SMALL_RESULT query" in {
    BasicTable.select.sqlSmallResult
      .queryString shouldEqual "SELECT SQL_SMALL_RESULT * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT SQL_SMALL_RESULT query with an WHERE clause" in {
    BasicTable.select.sqlSmallResult.where(_.name eqs "test")
      .queryString shouldEqual "SELECT SQL_SMALL_RESULT * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with a single column in the partial select" in {
    BasicTable.select(_.name)
      .sqlSmallResult
      .queryString shouldEqual "SELECT SQL_SMALL_RESULT name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name)
      .sqlSmallResult
      .where(_.count >= 5).queryString shouldEqual "SELECT SQL_SMALL_RESULT name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count)
      .sqlSmallResult
      .queryString shouldEqual "SELECT SQL_SMALL_RESULT name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count)
      .sqlSmallResult
      .where(_.count <= 10).queryString shouldEqual "SELECT SQL_SMALL_RESULT name, count FROM `BasicTable` WHERE count <= 10;"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlSmallResult
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT SQL_SMALL_RESULT name, count FROM `BasicTable` WHERE count >= 10 AND count <= 100;"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlSmallResult
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT SQL_SMALL_RESULT name, " +
      "count FROM `BasicTable` WHERE count >= 10 AND (count <= 100 OR name = 'test');"
  }
  //

  it should "serialise a simple SELECT SQL_BIG_RESULT query" in {
    BasicTable.select.sqlBigResult.queryString shouldEqual "SELECT SQL_BIG_RESULT * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT SQL_BIG_RESULT query with an WHERE clause" in {
    BasicTable.select.sqlBigResult.where(_.name eqs "test")
      .queryString shouldEqual "SELECT SQL_BIG_RESULT * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with a single column in the partial select" in {
    BasicTable.select(_.name).sqlBigResult.queryString shouldEqual "SELECT SQL_BIG_RESULT name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).sqlBigResult.where(_.count >= 5)
      .queryString shouldEqual "SELECT SQL_BIG_RESULT name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).sqlBigResult.queryString shouldEqual "SELECT SQL_BIG_RESULT name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).sqlBigResult.where(_.count <= 10)
      .queryString shouldEqual "SELECT SQL_BIG_RESULT name, count FROM `BasicTable` WHERE count <= 10;"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlBigResult
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT SQL_BIG_RESULT name, count FROM `BasicTable` WHERE count >= 10 AND count <= 100;"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlBigResult
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT SQL_BIG_RESULT name, " +
      "count FROM `BasicTable` WHERE count >= 10 AND (count <= 100 OR name = 'test');"
  }

  it should "serialise a simple SELECT SQL_BUFFER_RESULT query" in {
    BasicTable.select.sqlBufferResult.queryString shouldEqual "SELECT SQL_BUFFER_RESULT * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT SQL_BUFFER_RESULT query with an WHERE clause" in {
    BasicTable.select.sqlBufferResult.where(_.name eqs "test")
      .queryString shouldEqual "SELECT SQL_BUFFER_RESULT * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with a single column in the partial select" in {
    BasicTable.select(_.name).sqlBufferResult.queryString shouldEqual "SELECT SQL_BUFFER_RESULT name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).sqlBufferResult.where(_.count >= 5)
      .queryString shouldEqual "SELECT SQL_BUFFER_RESULT name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).sqlBufferResult.queryString shouldEqual "SELECT SQL_BUFFER_RESULT name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).sqlBufferResult.where(_.count <= 10)
      .queryString shouldEqual "SELECT SQL_BUFFER_RESULT name, count FROM `BasicTable` WHERE count <= 10;"
  }

  it should "serialise a simple SELECT SQL_CACHE query" in {
    BasicTable.select.sqlCache.queryString shouldEqual "SELECT SQL_CACHE * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT SQL_CACHE query with an WHERE clause" in {
    BasicTable.select.sqlCache.where(_.name eqs "test").queryString shouldEqual "SELECT SQL_CACHE * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT SQL_CACHE query with a single column in the partial select" in {
    BasicTable.select(_.name).sqlCache.queryString shouldEqual "SELECT SQL_CACHE name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_CACHE query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).sqlCache.where(_.count >= 5).queryString shouldEqual "SELECT SQL_CACHE name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT SQL_CACHE query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).sqlCache.queryString shouldEqual "SELECT SQL_CACHE name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_CACHE query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).sqlCache.where(_.count <= 10).queryString shouldEqual "SELECT SQL_CACHE name, count FROM `BasicTable` WHERE count <= 10;"
  }

  it should "serialise a simple SELECT SQL_NO_CACHE query" in {
    BasicTable.select.sqlNoCache.queryString shouldEqual "SELECT SQL_NO_CACHE * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT SQL_NO_CACHE query with an WHERE clause" in {
    BasicTable.select.sqlNoCache.where(_.name eqs "test").queryString shouldEqual "SELECT SQL_NO_CACHE * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT SQL_NO_CACHE query with a single column in the partial select" in {
    BasicTable.select(_.name).sqlNoCache.queryString shouldEqual "SELECT SQL_NO_CACHE name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_NO_CACHE query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).sqlNoCache.where(_.count >= 5).queryString shouldEqual "SELECT SQL_NO_CACHE name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT SQL_NO_CACHE query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).sqlNoCache.queryString shouldEqual "SELECT SQL_NO_CACHE name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_NO_CACHE query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).sqlNoCache.where(_.count <= 10).queryString shouldEqual "SELECT SQL_NO_CACHE name, count FROM `BasicTable` WHERE count <= 10;"
  }


  it should "serialise a simple SELECT SQL_CALC_FOUND_ROWS query" in {
    BasicTable.select.sqlCalcFoundRows.queryString shouldEqual "SELECT SQL_CALC_FOUND_ROWS * FROM `BasicTable`;"
  }

  it should "serialise a simple SELECT SQL_CALC_FOUND_ROWS query with an WHERE clause" in {
    BasicTable.select.sqlCalcFoundRows.where(_.name eqs "test").queryString shouldEqual "SELECT SQL_CALC_FOUND_ROWS * FROM `BasicTable` WHERE name = 'test';"
  }

  it should "serialise a partial SELECT SQL_CALC_FOUND_ROWS query with a single column in the partial select" in {
    BasicTable.select(_.name).sqlCalcFoundRows.queryString shouldEqual "SELECT SQL_CALC_FOUND_ROWS name FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_CALC_FOUND_ROWS query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).sqlCalcFoundRows.where(_.count >= 5).queryString shouldEqual "SELECT SQL_CALC_FOUND_ROWS name FROM `BasicTable` WHERE count >= 5;"
  }

  it should "serialise a partial SELECT SQL_CALC_FOUND_ROWS query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).sqlCalcFoundRows.queryString shouldEqual "SELECT SQL_CALC_FOUND_ROWS name, count FROM `BasicTable`;"
  }

  it should "serialise a partial SELECT SQL_CALC_FOUND_ROWS query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).sqlCalcFoundRows.where(_.count <= 10)
      .queryString shouldEqual "SELECT SQL_CALC_FOUND_ROWS name, count FROM `BasicTable` WHERE count <= 10;"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlBufferResult
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT SQL_BUFFER_RESULT name, count FROM `BasicTable` WHERE count >= 10 AND count <= 100;"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlBufferResult
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT SQL_BUFFER_RESULT name, " +
      "count FROM `BasicTable` WHERE count >= 10 AND (count <= 100 OR name = 'test');"
  }

}
