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

import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.mysql._
import com.websudos.morpheus.query._
import com.websudos.morpheus.query.parts.{LightweightPart, ValuePart, Defaults, ColumnsPart}

private[morpheus] class MySQLInsertSyntaxBlock(query: String, tableName: String) extends RootInsertSyntaxBlock(query, tableName) {
  override val syntax = MySQLSyntax

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


class MySQLRootInsertQuery[T <: BaseTable[T, _, MySQLRow], R](table: T, st: MySQLInsertSyntaxBlock, rowFunc: MySQLRow => R)
  extends RootInsertQuery[T, R, MySQLRow](table, st, rowFunc) {

  def delayed: MySQLInsertQuery.Default[T, R] = {
    new MySQLInsertQuery(table, st.delayed, rowFunc)
  }

  def lowPriority: MySQLInsertQuery.Default[T, R] = {
    new MySQLInsertQuery(table, st.lowPriority, rowFunc)
  }

  def highPriority: MySQLInsertQuery.Default[T, R] = {
    new MySQLInsertQuery(table, st.highPriority, rowFunc)
  }

  def ignore: MySQLInsertQuery.Default[T, R] = {
    new MySQLInsertQuery(table, st.ignore, rowFunc)
  }

}

class MySQLInsertQuery[T <: BaseTable[T, _, MySQLRow],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](table: T,
  override val init: SQLBuiltQuery,
  rowFunc: MySQLRow => R,
  columnsPart: ColumnsPart = Defaults.EmptyColumnsPart,
  valuePart: ValuePart = Defaults.EmptyValuePart,
  lightweightPart: LightweightPart = Defaults.EmptyLightweightPart
) extends InsertQuery[T, R, MySQLRow, Group, Order, Limit, Chain, AssignChain, Status](table: T, init, rowFunc) {}

object MySQLInsertQuery {
  type Default[T <: BaseTable[T, _, MySQLRow], R] = MySQLInsertQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]
}