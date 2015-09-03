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

import com.websudos.morpheus.builder.{SQLBuiltQuery, AbstractSyntaxBlock, DefaultSQLSyntax, AbstractSQLSyntax}
import com.websudos.morpheus.column.{AbstractColumn, ForeignKeyDefinition}
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.sql.DefaultRow
import com.websudos.morpheus.{Row, SQLPrimitive}

import scala.annotation.implicitNotFound


private[morpheus] class AbstractSelectSyntaxBlock(
  query: String, tableName: String,
  columns: List[String] = List("*")) extends AbstractSyntaxBlock {

  protected[this] val qb = SQLBuiltQuery(query)

  def `*`: SQLBuiltQuery = {
    qb.pad.append(columns.mkString(" "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def all: SQLBuiltQuery = this.`*`

  def distinct: SQLBuiltQuery = {
    qb.pad.append(syntax.distinct)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  override def syntax: AbstractSQLSyntax = DefaultSQLSyntax
}


/**
 * This is the implementation of a root select query, a wrapper around an abstract syntax block.
 * The basic select of select methods can be seen in {@link com.websudos.morpheus.dsl.SelectTable}
 *
 * This is used as the entry point to an SQL query, and it requires the user to provide "one more method" to fully specify a SELECT query.
 * The implicit conversion from a RootSelectQuery to a SelectQuery will automatically pick the "all" strategy below.
 *
 * @param table The table owning the record.
 * @param st The Abstract syntax block describing the possible decisions.
 * @param rowFunc The function used to map a result to a type-safe record.
 * @tparam T The type of the owning table.
 * @tparam R The type of the record.
 */
private[morpheus] class AbstractRootSelectQuery[T <: BaseTable[T, _, TableRow], R, TableRow <: Row](val table: T, val st: AbstractSelectSyntaxBlock, val
rowFunc: TableRow => R) {

  def fromRow(r: TableRow): R = rowFunc(r)

  def distinct: SelectQuery[T, R, TableRow, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new SelectQuery(table, st.distinct, rowFunc)
  }

  def all: SelectQuery[T, R, TableRow, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new SelectQuery(table, st.*, rowFunc)
  }
}

private[morpheus] class DefaultRootSelectQuery[T <: BaseTable[T, _, DefaultRow], R](table: T, st: AbstractSelectSyntaxBlock,
  rowFunc: DefaultRow => R) extends AbstractRootSelectQuery[T, R, DefaultRow](table, st, rowFunc) {

}

/**
 * This bit of magic allows all extending sub-classes to implement the "set" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments,
 * all DSL representations of an UPDATE query can use the implementation without violating DRY.
 *
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
class SelectQuery[T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](table: T, query: SQLBuiltQuery, rowFunc: TableRow => R) extends Query[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status](table, query,
  rowFunc) {

  @implicitNotFound("You cannot use two where clauses on a single query")
  def where(condition: T => QueryCondition)(implicit ev: Chain =:= Unchainned): SelectQuery[T, R, TableRow, Group, Order, Limit, Chainned, AssignChain,
    Status] = {
    new SelectQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  def where(condition: QueryCondition)(implicit ev: Chain =:= Unchainned): SelectQuery[T, R, TableRow, Group, Order, Limit, Chainned, AssignChain, Status] = {
    new SelectQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  def and(condition: T => QueryCondition)(implicit ev: Chain =:= Chainned): SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChainned, Status]  = {
    new SelectQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  def and(condition: QueryCondition)(implicit ev: Chain =:= Chainned): SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChainned, Status] = {
    new SelectQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  final def having(condition: T => QueryCondition): SelectQuery[T, R, TableRow, Group,
    Order,
    Limit,
    Chain,
    AssignChainned, Status] = {
    new SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChainned, Status](
      table,
      table.queryBuilder.having(query, condition(table).clause),
      rowFunc
    )
  }

  @inline
  private[this] def joinBuilder[Owner <: BaseTable[Owner, Record, TableRow], Record](
      joiner: (SQLBuiltQuery, String) => SQLBuiltQuery,
      join: BaseTable[Owner, Record, TableRow]): OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated] = {

    def fromRow(row: Row): (R, Record) = fromRow(row)

    new OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated](
      new SelectQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated](
        table,
        joiner(query, join.tableName),
        fromRow
      )
    )
  }

  def innerJoin[Owner <: BaseTable[Owner, Record, TableRow], Record](join: BaseTable[Owner, Record, TableRow])
      : OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated] =
    joinBuilder(table.queryBuilder.innerJoin, join)

  def leftJoin[Owner <: BaseTable[Owner, Record, TableRow], Record](join: BaseTable[Owner, Record, TableRow])
      : OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated] =
    joinBuilder(table.queryBuilder.leftJoin, join)

  def rightJoin[Owner <: BaseTable[Owner, Record, TableRow], Record](join: BaseTable[Owner, Record, TableRow])
      : OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated] =
    joinBuilder(table.queryBuilder.rightJoin, join)

  private[morpheus] final def terminate: SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Terminated] = {
    new SelectQuery(table, query, fromRow)
  }
}

sealed case class JoinClause(clause: SQLBuiltQuery)

/**
 * This bit of magic allows all extending sub-classes to implement the "set" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments,
 * all DSL representations of an UPDATE query can use the implementation without violating DRY.
 *
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
class OnJoinQuery[T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](val query: SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status]) {

  def on(condition: T => JoinClause): SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status] = {
    new SelectQuery(
      query.table,
      query.table.queryBuilder.on(query.query, condition(query.table).clause),
      query.fromRow
    )
  }
}

private[morpheus] trait JoinImplicits {

  implicit class JoinColumn[T : SQLPrimitive](val origin: AbstractColumn[T] with ForeignKeyDefinition) {

    final def eqs(col: AbstractColumn[_]): JoinClause = {
      JoinClause(
        origin.table.queryBuilder.eqs(
          s"${origin.table.tableName}.${origin.name}",
          s"${col.table.tableName}.${col.name}"
        )
      )
    }
  }

}
