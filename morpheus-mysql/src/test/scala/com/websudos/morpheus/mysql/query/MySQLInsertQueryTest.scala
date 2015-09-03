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

import com.websudos.morpheus.mysql.tables.BasicTable
import org.scalatest.{FlatSpec, Matchers}

class  MySQLInsertQueryTest extends FlatSpec with Matchers {
  it should "serialise an INSERT IGNORE INTO query to the correct query" in {
    BasicTable.insert.ignore.queryString shouldEqual "INSERT IGNORE INTO BasicTable;"
  }

  it should "serialise an INSERT HIGH_PRIORITY INTO query to the correct query" in {
    BasicTable.insert.highPriority.queryString shouldEqual "INSERT HIGH_PRIORITY INTO BasicTable;"
  }

  it should "serialise an INSERT LOW_PRIORITY INTO query to the correct query" in {
    BasicTable.insert.lowPriority.queryString shouldEqual "INSERT LOW_PRIORITY INTO BasicTable;"
  }

  it should "serialise an INSERT DELAYED INTO query to the correct query" in {
    BasicTable.insert.delayed.queryString shouldEqual "INSERT DELAYED INTO BasicTable;"
  }
}
