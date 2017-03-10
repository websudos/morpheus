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
import com.outworkers.morpheus.column.AbstractColumn
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine.query.parts.{ColumnsPart, Defaults, LightweightPart, ValuePart}
import com.outworkers.morpheus.Row
import shapeless.{HList, HNil}

import scala.annotation.implicitNotFound

private[morpheus] class RootInsertSyntaxBlock(query: String, tableName: String) extends AbstractSyntaxBlock {

  protected[this] val qb = SQLBuiltQuery(query)

  def into: SQLBuiltQuery = {
    qb.forcePad.append(syntax.into)
      .forcePad.appendEscape(tableName)
  }

  override val syntax: AbstractSQLSyntax = DefaultSQLSyntax
}

/**
 * This is the implementation of a root UPDATE query, a wrapper around an abstract syntax block.
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
private[morpheus] class RootInsertQuery[T <: BaseTable[T, _, TableRow], R, TableRow <: Row](val table: T, val st: RootInsertSyntaxBlock, val rowFunc:
TableRow => R) {

  def fromRow(r: TableRow): R = rowFunc(r)

  private[morpheus] def into: InsertQuery[T, R, TableRow, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new InsertQuery(table, st.into, rowFunc)
  }
}

private[morpheus] class DefaultRootInsertQuery[T <: BaseTable[T, _, DefaultRow], R]
(table: T, st: RootInsertSyntaxBlock, rowFunc: DefaultRow => R)
  extends RootInsertQuery[T, R, DefaultRow](table, st, rowFunc) {}


class InsertQuery[
  T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](table: T,
  init: SQLBuiltQuery,
  rowFunc: TableRow => R,
  columnsPart: ColumnsPart = Defaults.EmptyColumnsPart,
  valuePart: ValuePart = Defaults.EmptyValuePart,
  lightweightPart: LightweightPart = Defaults.EmptyLightweightPart
) extends Query[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status](table, init, rowFunc) {

  protected[this] type QueryType[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ] = InsertQuery[T, R, TableRow, G, O, L, S, C, P]

  override protected[this] def create[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ](t: T, q: SQLBuiltQuery, r: TableRow => R): QueryType[G, O, L, S, C, P] = {
    new InsertQuery(t, q, r, columnsPart, valuePart, lightweightPart)
  }

  /**
   * At this point you may be reading and thinking "WTF", but fear not, it all makes sense. Every call to a "value method" will generate a new Insert Query,
   * but the list of statements in the new query will include a new (String, String) pair, where the first part is the column name and the second one is the
   * serialised value. This is a very simple accumulator that will eventually allow calling the "insert" method on a queryBuilder to produce the final
   * serialisation result, a hopefully valid MySQL insert query.
   *
   * @param insertion The insert condition is a pair of a column with the value to use for it. It looks like this: value(_.someColumn, someValue),
   *                  where the assignment is of course type safe.
   * @param obj The object is the value to use for the column.
   * @tparam RR The SQL primitive or rather it's Scala correspondent to use at this time.
   * @return A new InsertQuery, where the list of statements in the Insert has been chained and updated for serialisation.
   */
  @implicitNotFound(msg = "To use the value method this query needs to be an insert query and the query needs to be HNil. You probably have more " +
    "value calls than columns in your table, which would result in an invalid MySQL query.")
  def value[RR : DataType](
    insertion: T => AbstractColumn[RR],
    obj: RR
  ): QueryType[Group, Order, Limit, Chain, AssignChain, Status] = {
    new InsertQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status](
      table,
      init,
      fromRow,
      columnsPart append SQLBuiltQuery(insertion(table).name),
      valuePart append SQLBuiltQuery(implicitly[DataType[RR]].serialize(obj)),
      lightweightPart
    )
  }

  override def query: SQLBuiltQuery = (columnsPart merge valuePart merge lightweightPart) build init
}
