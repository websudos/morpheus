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
package com.outworkers.morpheus.sql

import com.outworkers.morpheus.engine.query._
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.column.{AbstractColumn, AbstractModifyColumn, Column, SelectColumn}
import com.outworkers.morpheus.{Row => MorpheusRow}
import shapeless.HNil

private[morpheus] trait DefaultSQLImplicits extends JoinImplicits {

  implicit class SelectColumnRequired[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: MorpheusRow, T](
    col: Column[Owner, Record, TableRow, T]
  ) extends SelectColumn[T](SQLBuiltQuery(col.name)) {
    def apply(r: MorpheusRow): T = col.apply(r)
  }


  implicit class ModifyColumn[RR: DataType](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col)
  implicit class OrderingColumn[RR: DataType](col: AbstractColumn[RR]) extends AbstractOrderingColumn[RR](col)

  implicit def selectOperatorClauseToSelectColumn[T](clause: SelectOperatorClause[T]): SelectColumn[T] = new SelectColumn[T](clause.qb) {
    def apply(row: MorpheusRow): T = clause.fromRow(row)
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
  implicit def rootUpdateQueryToUpdateQuery[T <: BaseTable[T, R, DefaultRow], R](root: DefaultRootUpdateQuery[T, R]): UpdateQuery[T, R, DefaultRow, Ungroupped,
    Unordered,
    Unlimited,
    Unchainned, AssignUnchainned, HNil
    ] = {
    new UpdateQuery(
      root.table,
      root.st.all,
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
  implicit def rootDeleteQueryToDeleteQuery[T <: BaseTable[T, R, TR], R, TR <: MorpheusRow](root: RootDeleteQuery[T, R, TR]): DeleteQuery[T,
    R,
    TR,
    Ungroupped,
    Unordered,
    Unlimited,
    Unchainned,
    AssignUnchainned,
    HNil
    ] = {
    new DeleteQuery(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootSelectQuery to a SelectQuery, making the SELECT syntax block invisible to the end user.
   * Much like a decision block, a SelectSyntaxBlock needs a decision branch to follow, may that be DISTINCT, ALL or DISTINCTROW as per the SQL spec.
   *
   * The one catch is that this form of "exit" from an un-executable RootSelectQuery will directly translate the query to a "SELECT fields* FROM tableName"
   * query, meaning no SELECT operators will be used in the serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method, such as "all", "distinct" or "distinctrow",
   * the desired behaviour is a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootSelectQueryToSelectQuery[T <: BaseTable[T, _, DefaultRow], R](root: DefaultRootSelectQuery[T, R]): SelectQuery[T, R, DefaultRow, Ungroupped,
    Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = root.all

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
  implicit def rootInsertQueryToQuery[T <: BaseTable[T, R, DefaultRow], R](root: DefaultRootInsertQuery[T, R]): InsertQuery[T, R, DefaultRow, Ungroupped,
    Unordered,
    Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new InsertQuery(
      root.table,
      root.st.into,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootInsertQuery to an InsertQuery, making the INSERT syntax block invisible to the end user.
   * This allows chaining a "value" method call directly after "Table.insert".
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootCreateQueryToCreateQuery[T <: BaseTable[T, R, TR], R, TR <: MorpheusRow](root: RootCreateQuery[T, R, TR]): CreateQuery[T, R, TR, Ungroupped,
    Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new CreateQuery(
      root.table,
      root.st.default,
      root.rowFunc
    )
  }
}
