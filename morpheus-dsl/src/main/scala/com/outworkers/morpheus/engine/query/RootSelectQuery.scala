/*
 * Copyright 2013 - 2017 Outworkers Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.outworkers.morpheus.engine.query

import com.outworkers.morpheus.DataType
import com.outworkers.morpheus.sql.DefaultRow
import com.outworkers.morpheus.builder.{AbstractSQLSyntax, AbstractSyntaxBlock, DefaultSQLSyntax, SQLBuiltQuery}
import com.outworkers.morpheus.column.{AbstractColumn, ForeignKeyDefinition}
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.Row
import shapeless.{HList, HNil}

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
 * The basic select of select methods can be seen in {@link com.outworkers.morpheus.dsl.SelectTable}
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
private[morpheus] class AbstractRootSelectQuery[
  T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row
](val table: T, val st: AbstractSelectSyntaxBlock, val rowFunc: TableRow => R) {

  def fromRow(r: TableRow): R = rowFunc(r)

  def distinct: SelectQuery[T, R, TableRow, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new SelectQuery(table, st.distinct, rowFunc)
  }

  def all: SelectQuery[T, R, TableRow, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
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
  Status <: HList
](table: T, init: SQLBuiltQuery, rowFunc: TableRow => R)
  extends Query[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status](table, init, rowFunc) {

  protected[this] type QueryType[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ] = SelectQuery[T, R, TableRow, G, O, L, S, C, P]

  override protected[this] def create[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
    ](t: T, q: SQLBuiltQuery, r: TableRow => R): QueryType[G, O, L, S, C, P] = {
    new SelectQuery(t, q, r)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  def where(condition: T => QueryCondition)(
    implicit ev: Chain =:= Unchainned
  ): QueryType[Group, Order, Limit, Chainned, AssignChain, Status] = {
    new SelectQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  def where(condition: QueryCondition)(
    implicit ev: Chain =:= Unchainned
  ): QueryType[Group, Order, Limit, Chainned, AssignChain, Status] = {
    new SelectQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  def and(condition: T => QueryCondition)(
    implicit ev: Chain =:= Chainned
  ): QueryType[Group, Order, Limit, Chain, AssignChainned, Status]  = {
    new SelectQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  def and(condition: QueryCondition)(
    implicit ev: Chain =:= Chainned
  ): QueryType[Group, Order, Limit, Chain, AssignChainned, Status] = {
    new SelectQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  final def having(condition: T => QueryCondition): QueryType[Group, Order, Limit, Chain, AssignChainned, Status] = {
    new SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChainned, Status](
      table,
      table.queryBuilder.having(query, condition(table).clause),
      rowFunc
    )
  }

  @inline
  private[this] def joinBuilder[Owner <: BaseTable[Owner, Record, TableRow], Record](
      joiner: (SQLBuiltQuery, String) => SQLBuiltQuery,
      join: BaseTable[Owner, Record, TableRow]
  ): OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, HNil] = {

    def fromRow(row: Row): (R, Record) = fromRow(row)

    new OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, HNil](
      new SelectQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, HNil](
        table,
        joiner(query, join.tableName),
        fromRow
      )
    )
  }

  def innerJoin[Owner <: BaseTable[Owner, Record, TableRow], Record](join: BaseTable[Owner, Record, TableRow])
      : OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, HNil] =
    joinBuilder(table.queryBuilder.innerJoin, join)

  def leftJoin[Owner <: BaseTable[Owner, Record, TableRow], Record](join: BaseTable[Owner, Record, TableRow])
      : OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, HNil] =
    joinBuilder(table.queryBuilder.leftJoin, join)

  def rightJoin[Owner <: BaseTable[Owner, Record, TableRow], Record](join: BaseTable[Owner, Record, TableRow])
      : OnJoinQuery[T, (R, Record), TableRow, Group, Order, Limit, Chain, AssignChain, HNil] =
    joinBuilder(table.queryBuilder.rightJoin, join)

  override protected[morpheus] def query: SQLBuiltQuery = init
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
  Status <: HList
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

  implicit class JoinColumn[T : DataType](val origin: AbstractColumn[T] with ForeignKeyDefinition) {

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
