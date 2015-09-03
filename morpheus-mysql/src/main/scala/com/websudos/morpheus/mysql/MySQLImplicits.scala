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

package com.websudos.morpheus.mysql

import com.websudos.morpheus.{ Row => MorpheusRow }
import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.column.{AbstractColumn, AbstractModifyColumn, Column, SelectColumn}
import com.websudos.morpheus.mysql.query.{MySQLUpdateQuery, MySQLInsertQuery, MySQLRootInsertQuery, MySQLRootUpdateQuery}
import com.websudos.morpheus.query._

trait MySQLImplicits extends DefaultSQLEngines {

  implicit class SelectColumnRequired[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: MorpheusRow,  T](col: Column[Owner, Record, TableRow, T])
    extends SelectColumn[T](SQLBuiltQuery(col.name)) {
    def apply(r: MorpheusRow): T = col.apply(r)
  }


  implicit class ModifyColumn[RR: SQLPrimitive](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col)
  implicit class OrderingColumn[RR: SQLPrimitive](col: AbstractColumn[RR]) extends AbstractOrderingColumn[RR](col)

  implicit def selectOperatorClauseToSelectColumn[T](clause: SelectOperatorClause[T]): SelectColumn[T] = new SelectColumn[T](clause.qb) {
    def apply(row: MorpheusRow): T = clause.fromRow(row)
  }

  /**
   * This defines an implicit conversion from a RootInsertQuery to an InsertQuery, making the INSERT syntax block invisible to the end user.
   * This is used to automatically "exit" the INSERT syntax block with the default "INSERT INTO" option, while picking no other SQL options such as IGNORE or
   * LOW_PRIORITY.
   *
   * This is making the following queries equivalent:
   * - Table.insert.into.queryString = "INSERT INTO table"
   * - Table.insert = "INSERT INTO table"
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootInsertQueryToQuery[T <: BaseTable[T, _, MySQLRow], R](root: MySQLRootInsertQuery[T, R]): MySQLInsertQuery[T, R, Ungroupped,
    Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLInsertQuery(
      root.table,
      root.st.into,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootUpdateQuery to an UpdateQuery, making the UPDATE syntax block invisible to the end user.
   * Much like a decision block, a UpdateSyntaxBlock needs a decision branch to follow, may that be nothing, LOW_PRIORITY or IGNORE.
   *
   * The one catch is that this form of "exit" from an un-executable RootUpdateQuery will directly translate the query to an "UPDATE tableName"
   * query, meaning no UPDATE operators will be used in the default serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method, such as "lowPriority" or "ignore" the desired behaviour is
   * a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def defaultUpdateQueryToUpdateQuery[T <: BaseTable[T, R, MySQLRow], R](root: MySQLRootUpdateQuery[T, R]): MySQLUpdateQuery[T, R,
    Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLUpdateQuery(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  implicit def updateQueryToAssignmentsQuery[T <: BaseTable[T, R, MySQLRow],
    R,
    Group <: GroupBind,
    Order <: OrderBind,
    Limit <: LimitBind,
    Chain <: ChainBind,
    AssignChain <: AssignBind,
    Status <: StatusBind
  ](query: MySQLUpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status]): MySQLUpdateQuery[T, R, Group, Order, Limit, Chain,
    AssignChain, Status] = {
    new MySQLUpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status](query.table, query.query, query.rowFunc)
  }

}
