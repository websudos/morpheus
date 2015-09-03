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

import com.websudos.morpheus.Row
import com.websudos.morpheus.builder.{SQLBuiltQuery, AbstractSyntaxBlock, DefaultSQLSyntax, AbstractSQLSyntax}
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.sql.DefaultRow

private[morpheus] class RootCreateSyntaxBlock(query: String, tableName: String) extends AbstractSyntaxBlock {

  protected[this] val qb: SQLBuiltQuery = SQLBuiltQuery(query)

  def syntax: AbstractSQLSyntax = DefaultSQLSyntax

  def default: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLSyntax.table)
      .forcePad.appendEscape(tableName)
  }

  def ifNotExists: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLSyntax.table)
      .forcePad.append(syntax.ifNotExists)
      .forcePad.appendEscape(tableName)
  }

  def temporary: SQLBuiltQuery = {
    qb.pad
      .append(syntax.temporary)
      .forcePad.append(DefaultSQLSyntax.table)
      .forcePad.appendEscape(tableName)
  }
}

/**
 * This is the implementation of a root CREATE query, a wrapper around an abstract CREATE syntax block.
 *
 * This is used as the entry point to an SQL CREATE query, and it requires the user to provide "one more method" to fully specify a CREATE query.
 * The implicit conversion from a RootCreateQuery to a CreateQuery will automatically pick the "default" strategy below.
 *
 * @param table The table owning the record.
 * @param st The Abstract syntax block describing the possible decisions.
 * @param rowFunc The function used to map a result to a type-safe record.
 * @tparam T The type of the owning table.
 * @tparam R The type of the record.
 */
private[morpheus] class RootCreateQuery[T <: BaseTable[T, _, TableRow], R, TableRow <: Row](val table: T, val st: RootCreateSyntaxBlock, val rowFunc:
TableRow => R) {

  protected[this] type BaseCreateQuery = CreateQuery[T, R, TableRow, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]

  private[morpheus] def default: BaseCreateQuery = {
    new CreateQuery(table, st.default, rowFunc)
  }

  def ifNotExists: BaseCreateQuery = {
    new CreateQuery(table, st.ifNotExists, rowFunc)
  }

  def temporary: BaseCreateQuery = {
    new CreateQuery(table, st.temporary, rowFunc)
  }
}

private[morpheus] class DefaultRootCreateQuery[T <: BaseTable[T, _, DefaultRow], R]
(table: T, st: RootCreateSyntaxBlock, rowFunc: DefaultRow => R)
  extends RootCreateQuery[T, R, DefaultRow](table, st, rowFunc) {}


/**
 * This bit of magic allows all extending sub-classes to implement the "set" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments,
 * all DSL representations of an UPDATE query can use the implementation without violating DRY.
 *
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
class CreateQuery[T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](table: T, query: SQLBuiltQuery, rowFunc: TableRow => R) extends Query[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status](table, query, rowFunc) {

  final protected[morpheus] def columnDefinitions: List[String] = {
    table.columns.foldRight(List.empty[String])((col, acc) => {
      col.qb.queryString :: acc
    })
  }

  final protected[morpheus] def columnSchema[St <: StatusBind]: CreateQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, St] = {
    new CreateQuery(table, query.append(columnDefinitions.mkString(", ")), rowFunc)
  }

  def ifNotExists: CreateQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated] = {
    new CreateQuery(table, table.queryBuilder.ifNotExists(query), rowFunc)
  }

  def engine(engine: SQLEngine): Query[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated] = {
    new CreateQuery(table,
      table.queryBuilder.engine(
        query.wrap(columnDefinitions.mkString(", ")),
        engine.value
      ),
      rowFunc
    )
  }

  private[morpheus] final def terminate: CreateQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Terminated] = {
    new CreateQuery(
      table,
      query,
      rowFunc
    )
  }

}
