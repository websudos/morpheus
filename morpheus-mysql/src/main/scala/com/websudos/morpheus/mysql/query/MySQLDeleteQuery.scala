/*
 * Copyright 2014 websudos ltd.
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

package com.websudos.morpheus.mysql.query

import com.websudos.morpheus.builder.{SQLBuiltQuery, DefaultSQLSyntax}
import com.websudos.morpheus.mysql._
import com.websudos.morpheus.query._

case class MySQLDeleteSyntaxBlock(query: String, tableName: String) extends RootDeleteSyntaxBlock(query, tableName) {

  override val syntax = MySQLSyntax

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

private[morpheus] class MySQLRootDeleteQuery[T <: BaseTable[T, _, MySQLRow], R](table: T, st: MySQLDeleteSyntaxBlock, rowFunc: MySQLRow => R)
  extends RootDeleteQuery[T, R, MySQLRow](table, st, rowFunc) {

  def lowPriority: MySQLDeleteQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLDeleteQuery(table, st.lowPriority, rowFunc)
  }

  def ignore: MySQLDeleteQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLDeleteQuery(table, st.ignore, rowFunc)
  }
}

class MySQLDeleteQuery[T <: BaseTable[T, _, MySQLRow],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](table: T, query: SQLBuiltQuery, rowFunc: MySQLRow => R) extends DeleteQuery[T, R, MySQLRow, Group, Order, Limit, Chain, AssignChain, Status](table, query,
  rowFunc) {

}