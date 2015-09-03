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

import org.scalatest.{FlatSpec, Matchers}

import com.websudos.morpheus.dsl.{BasicRecord, BasicTable}
import com.websudos.morpheus.sql._
import com.websudos.morpheus.tables.{KeysRecord, KeysTable, IndexTable}

class CompileTimeRestrictionsTest extends FlatSpec with Matchers {

  it should " compile a SELECT query with a limit set before an orderBy clause" in {
    """BasicTable.select
      .limit(10)
      .orderBy(_.count asc, _.name desc)
    """ should compile
  }

  it should " compile a SELECT query with a limit set after an orderBy clause" in {
    """BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .limit(10)
    """ should compile
  }

  it should "not compile a SELECT query with a limit set before and after an orderBy clause" in {
    """BasicTable.select
      .limit(10)
      .orderBy(_.count asc, _.name desc)
      .limit(10)
    """ shouldNot compile
  }

  it should "not compile a SELECT query with two limit clauses" in {
    BasicTable.select
      .limit(10)

    """BasicTable.select
      .limit(10)
      .limit(10)
    """ shouldNot compile
  }

  it should " compile a SELECT query with a an orderBy clause set after a limit clause" in {
    """BasicTable.select
      .limit(10)
      .orderBy(_.count asc, _.name desc)
    """ should compile
  }

  it should " compile a SELECT query with a an orderBy clause set before a limit clause" in {
    """BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .limit(10)
    """ should compile
  }

  it should "not compile a SELECT query with a an orderBy clause set before and after a limit clause" in {
    """BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .limit(10)
    """ should compile
  }

  it should "not compile a SELECT query with two orderBy clauses" in {
    """BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .orderBy(_.count asc, _.name desc)
    """ shouldNot compile
  }

  it should "compile a SELECT query with a single groupBy clause" in {
    """BasicTable.select
      .groupBy(_.count, _.name)
    """ should compile
  }

  it should "not compile a SELECT query with two groupBy clauses" in {
    """BasicTable.select
      .groupBy(_.count, _.name)
      .groupBy(_.count, _.name)
    """ shouldNot compile
  }

  it should "compile a select query groupped before ordering" in {
    """BasicTable.select
      .groupBy(_.count, _.name)
      .orderBy(_.name asc)
      .queryString""" should compile
  }

  it should "not compile a select query ordered before grouping" in {
    """BasicTable.select
      .orderBy(_.name asc)
      .groupBy(_.count, _.name)
      .queryString""" shouldNot compile
  }

  it should "allow defining a foreignKey from one table to another" in {
    """object foreign extends ForeignKey[BasicTable, BasicRecord, IndexTable](BasicTable, IndexTable.id, IndexTable.value)""" should compile
  }

  it should "not allow defining a foreignKey from a table to columns in the same table" in {
    // this line is because InteliJ will remove the imports automatically as it's not detecting objects inside string literals.
    // We are also too lazy to remove the automatic import management.
    object foreign extends ForeignKey[BasicTable, BasicRecord, IndexTable](BasicTable, IndexTable.id, IndexTable.value)

    """object foreign2 extends ForeignKey[BasicTable, BasicRecord, BasicTable](BasicTable, BasicTable.name, BasicTable.count)""" shouldNot compile
  }

  it should "not allow defining a foreignKey from a table to columns belonging to multiple tables" in {
    """ object foreign extends ForeignKey[BasicTable, BasicRecord, IndexTable](BasicTable, IndexTable.id, KeysTable.id)""" shouldNot compile
  }

  it should "not allow defining a foreignKey from a table to column that is an index column" in {
    // This line is also because we are a lazy bunch.
    object foreign3 extends ForeignKey[BasicTable, BasicRecord, KeysTable](BasicTable, KeysTable.id)
    """ object foreign4 extends ForeignKey[BasicTable, BasicRecord, KeysTable](BasicTable, KeysTable.foreignKey, KeysTable.id)""" shouldNot compile
  }


  it should "not allow defining an index on a column that is a foreignKey" in {
    // This line is also because we are a lazy bunch.
    object index1 extends Index[KeysTable, KeysRecord](KeysTable.id)
    """object foreign3 extends Index[KeysTable, KeysRecord](KeysTable.id, KeysTable.foreignKey)""" shouldNot compile
  }

}
