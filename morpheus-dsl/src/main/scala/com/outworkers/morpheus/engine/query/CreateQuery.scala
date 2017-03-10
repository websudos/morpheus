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

import com.outworkers.morpheus.Row
import com.outworkers.morpheus.sql.DefaultRow
import com.outworkers.morpheus.builder.{AbstractSQLSyntax, AbstractSyntaxBlock, DefaultSQLSyntax, SQLBuiltQuery}
import com.outworkers.morpheus.dsl.BaseTable
import shapeless.{HList, HNil}

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
private[morpheus] class RootCreateQuery[
  T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row
](val table: T, val st: RootCreateSyntaxBlock, val rowFunc: TableRow => R) {

  protected[this] type BaseCreateQuery = CreateQuery[T, R, TableRow, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil]

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
  Status <: HList
](table: T, init: SQLBuiltQuery, rowFunc: TableRow => R) extends Query[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status](table, init, rowFunc) {

  override def query: SQLBuiltQuery = init

  protected[this] type QueryType[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ] = CreateQuery[T, R, TableRow, G, O, L, S, C, P]

  override protected[this] def create[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ](t: T, q: SQLBuiltQuery, r: TableRow => R): QueryType[G, O, L, S, C, P] = {
    new CreateQuery(t, q, r)
  }


  final protected[morpheus] def columnDefinitions: List[String] = {
    table.columns.foldRight(List.empty[String])((col, acc) => {
      col.qb.queryString :: acc
    })
  }

  final protected[morpheus] def columnSchema[St <: HList]: CreateQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, St] = {
    new CreateQuery(table, init.append(columnDefinitions.mkString(", ")), rowFunc)
  }

  def ifNotExists: CreateQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, HNil] = {
    new CreateQuery(table, table.queryBuilder.ifNotExists(init), rowFunc)
  }

  def engine(engine: SQLEngine): Query[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, HNil] = {
    new CreateQuery(table,
      table.queryBuilder.engine(
        init.wrap(columnDefinitions.mkString(", ")),
        engine.value
      ),
      rowFunc
    )
  }
}
