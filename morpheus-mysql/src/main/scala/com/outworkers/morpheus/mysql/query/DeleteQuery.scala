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

import com.outworkers.morpheus.mysql.{Row, Syntax}
import com.outworkers.morpheus.engine
import com.outworkers.morpheus.engine.query.{AssignBind, AssignUnchainned}
import com.outworkers.morpheus.builder.{DefaultSQLSyntax, SQLBuiltQuery}
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine.query._
import shapeless.{HList, HNil}

case class DeleteSyntaxBlock(query: String, tableName: String) extends engine.query.RootDeleteSyntaxBlock(query, tableName) {

  override val syntax = Syntax

  private[this] def deleteOption(option: String, table: String): SQLBuiltQuery = {
    qb.pad.append(option)
      .forcePad.append(DefaultSQLSyntax.from)
      .forcePad.append(table)
  }


  def lowPriority: SQLBuiltQuery = {
    deleteOption(syntax.Priorities.lowPriority, tableName)
  }

  def ignore: SQLBuiltQuery = {
    deleteOption(syntax.DeleteOptions.ignore, tableName)
  }

  def quick: SQLBuiltQuery = {
    deleteOption(syntax.DeleteOptions.quick, tableName)
  }
}

private[morpheus] class RootDeleteQuery[T <: BaseTable[T, _, Row], R](table: T, st: DeleteSyntaxBlock, rowFunc: Row => R)
  extends engine.query.RootDeleteQuery[T, R, Row](table, st, rowFunc) {

  def lowPriority: DeleteQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new DeleteQuery(table, st.lowPriority, rowFunc)
  }

  def ignore: DeleteQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new DeleteQuery(table, st.ignore, rowFunc)
  }
}

class DeleteQuery[T <: BaseTable[T, _, Row],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](table: T, query: SQLBuiltQuery, rowFunc: Row => R) extends engine.query.DeleteQuery[T, R, Row, Group, Order, Limit, Chain, AssignChain, Status](table, query,
  rowFunc) {

}
