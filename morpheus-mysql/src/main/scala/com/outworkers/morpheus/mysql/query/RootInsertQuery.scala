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
package com.outworkers.morpheus.mysql.query

import com.outworkers.morpheus.DataType
import com.outworkers.morpheus.mysql.{Row, Syntax}
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.column.AbstractColumn
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine
import com.outworkers.morpheus.engine.query._
import com.outworkers.morpheus.engine.query.parts.{ColumnsPart, Defaults, LightweightPart, ValuePart}
import shapeless.{HList, HNil}

import scala.annotation.implicitNotFound

private[morpheus] class InsertSyntaxBlock(
  query: String,
  tableName: String
) extends engine.query.RootInsertSyntaxBlock(query, tableName) {
  override val syntax = Syntax

  private[this] def insertOption(option: String, table: String): SQLBuiltQuery = {
    qb.pad.append(option)
      .forcePad.append(syntax.into)
      .forcePad.append(table)
  }


  def delayed: SQLBuiltQuery = {
    insertOption(syntax.InsertOptions.delayed, tableName)
  }

  def lowPriority: SQLBuiltQuery = {
    insertOption(syntax.Priorities.lowPriority, tableName)
  }

  def highPriority: SQLBuiltQuery = {
    insertOption(syntax.Priorities.highPriority, tableName)
  }

  def ignore: SQLBuiltQuery = {
    insertOption(syntax.InsertOptions.ignore, tableName)
  }
}


class RootInsertQuery[T <: BaseTable[T, _, Row], R](table: T, st: InsertSyntaxBlock, rowFunc: Row => R)
  extends engine.query.RootInsertQuery[T, R, Row](table, st, rowFunc) {

  def delayed: InsertQuery.Default[T, R] = {
    new InsertQuery(table, st.delayed, rowFunc)
  }

  def lowPriority: InsertQuery.Default[T, R] = {
    new InsertQuery(table, st.lowPriority, rowFunc)
  }

  def highPriority: InsertQuery.Default[T, R] = {
    new InsertQuery(table, st.highPriority, rowFunc)
  }

  def ignore: InsertQuery.Default[T, R] = {
    new InsertQuery(table, st.ignore, rowFunc)
  }

}

class InsertQuery[T <: BaseTable[T, _, Row],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](table: T,
  override val init: SQLBuiltQuery,
  rowFunc: Row => R,
  columnsPart: ColumnsPart = Defaults.EmptyColumnsPart,
  valuePart: ValuePart = Defaults.EmptyValuePart,
  lightweightPart: LightweightPart = Defaults.EmptyLightweightPart
) extends engine.query.InsertQuery[T, R, Row, Group, Order, Limit, Chain, AssignChain, Status](table: T, init, rowFunc) {

  override def query: SQLBuiltQuery = (columnsPart merge valuePart merge lightweightPart) build init

  override protected[this] def create[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ](t: T, q: SQLBuiltQuery, r: Row => R): QueryType[G, O, L, S, C, P] = {
    new InsertQuery(t, q, r, columnsPart, valuePart, lightweightPart)
  }

  /**
    * At this point you may be reading and thinking "WTF", but fear not, it all makes sense.
    * Every call to a "value method" will generate a new Insert Query,
    * but the list of statements in the new query will include a new (String, String) pair,
    * where the first part is the column name and the second one is the
    * serialised value
    *
    * This is a very simple accumulator that will eventually allow calling the "insert" method on a queryBuilder to produce the final
    * serialisation result, a hopefully valid MySQL insert query.
    *
    * @param insertion The insert condition is a pair of a column with the value to use for it.
    *                  It looks like this: value(_.someColumn, someValue), where the assignment is of course type safe.
    * @param obj The object is the value to use for the column.
    * @tparam RR The SQL primitive or rather it's Scala correspondent to use at this time.
    * @return A new InsertQuery, where the list of statements in the Insert has been chained and updated for serialisation.
    */
  @implicitNotFound(msg = "To use the value method this query needs to be an insert query and the query needs to be unterminated. You probably have more " +
    "value calls than columns in your table, which would result in an invalid MySQL query.")
  override def value[RR : DataType](
    insertion: T => AbstractColumn[RR], obj: RR
  ): InsertQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status] = {
    new InsertQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status](
      table,
      init,
      fromRow,
      columnsPart append SQLBuiltQuery(insertion(table).name),
      valuePart append SQLBuiltQuery(implicitly[DataType[RR]].serialize(obj)),
      lightweightPart
    )
  }
}

object InsertQuery {
  type Default[T <: BaseTable[T, _, Row], R] = InsertQuery[
    T,
    R,
    Ungroupped,
    Unordered,
    Unlimited,
    Unchainned,
    AssignUnchainned,
    HNil
    ]
}
