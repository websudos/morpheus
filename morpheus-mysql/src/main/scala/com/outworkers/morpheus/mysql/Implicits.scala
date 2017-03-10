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
package com
package outworkers
package morpheus
package mysql

import com.outworkers.morpheus.mysql.query.{ RootInsertQuery, RootUpdateQuery, UpdateQuery}
import com.outworkers.morpheus.engine.query.{AssignBind, AssignUnchainned}
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.column.{AbstractColumn, AbstractModifyColumn, Column, SelectColumn}
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine.query._
import com.outworkers.morpheus.{Row => MorpheusRow}
import shapeless.{HList, HNil}

import scala.util.Try

trait Implicits extends DefaultSQLEngines {

  implicit class SelectColumnRequired[
    Owner <: BaseTable[Owner, Record, TableRow],
    Record, TableRow <: MorpheusRow, T
  ](col: Column[Owner, Record, TableRow, T]) extends SelectColumn[T](SQLBuiltQuery(col.name)) {
    def apply(r: morpheus.Row): T = col.apply(r)
  }

  implicit class ModifyColumn[RR : DataType](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col)
  implicit class OrderingColumn[RR : DataType](col: AbstractColumn[RR]) extends AbstractOrderingColumn[RR](col)

  implicit def selectOperatorClauseToSelectColumn[T](
    clause: SelectOperatorClause[T]
  ): SelectColumn[T] = new SelectColumn[T](clause.qb) {
    def apply(row: MorpheusRow): T = clause.fromRow(row)
  }

  /**
   * This defines an implicit conversion from a RootInsertQuery to an InsertQuery,
   * making the INSERT syntax block invisible to the end user.
   * This is used to automatically "exit" the INSERT syntax block with the default "INSERT INTO" option,
   * while picking no other SQL options such as IGNORE or
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
  implicit def rootInsertQueryToQuery[T <: BaseTable[T, _, mysql.Row], R](
    root: RootInsertQuery[T, R]
  ): mysql.query.InsertQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new mysql.query.InsertQuery(
      root.table,
      root.st.into,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootUpdateQuery to an UpdateQuery,
   * making the UPDATE syntax block invisible to the end user.
   * Much like a decision block, a UpdateSyntaxBlock needs a decision branch to follow, may that be nothing, LOW_PRIORITY or IGNORE.
   *
   * The one catch is that this form of "exit" from an un-executable RootUpdateQuery will directly translate the query to an "UPDATE tableName"
   * query, meaning no UPDATE operators will be used in the default serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method,
   * such as "lowPriority" or "ignore" the desired behaviour is
   * a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def defaultUpdateQueryToUpdateQuery[
    T <: BaseTable[T, R, Row],
    R
  ](root: RootUpdateQuery[T, R]): UpdateQuery[T, R,
    Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new UpdateQuery(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  implicit def updateQueryToAssignmentsQuery[T <: BaseTable[T, R, Row],
    R,
    Group <: GroupBind,
    Order <: OrderBind,
    Limit <: LimitBind,
    Chain <: ChainBind,
    AssignChain <: AssignBind,
    Status <: HList
  ](
    query: UpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status]
  ): UpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status] = {
    new UpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status](query.table, query.query, query.rowFunc)
  }

  def enumPrimitive[T <: Enumeration](enum: T)(implicit ev: DataType[String]): DataType[T#Value] = {
    new DataType[T#Value] {
      override val sqlType: String = ev.sqlType

      override def serialize(value: T#Value): String = ev.serialize(value.toString)

      override def deserialize(row: MorpheusRow, name: String): Try[T#Value] = {
        ev.deserialize(row, name) map (x => enum.withName(x))
      }
    }
  }


}
