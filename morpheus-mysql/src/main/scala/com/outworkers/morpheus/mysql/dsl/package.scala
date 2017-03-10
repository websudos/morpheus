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
package com.outworkers.morpheus
package mysql

import com.outworkers.morpheus.column.{AbstractColumn, DefaultForeignKeyConstraints}
import com.outworkers.morpheus.dsl.DefaultImportsDefinition
import com.outworkers.morpheus.engine.query._
import com.outworkers.morpheus.mysql.query.{SelectQuery, RootSelectQuery}
import com.outworkers.morpheus.operators.MySQLOperatorSet
import shapeless.HNil

import scala.util.Try

package object dsl extends DefaultImportsDefinition
  with Implicits
  with DataTypes
  with MySQLOperatorSet
  with Columns
  with Keys
  with PrimitiveColumns
  with DefaultForeignKeyConstraints {

  override implicit def columnToQueryColumn[T : DataType](col: AbstractColumn[T]): QueryColumn[T] = new QueryColumn[T](col)

  implicit def rootSelectQueryToQuery[T <: Table[T, _], R](
    root: RootSelectQuery[T, R]
  ): SelectQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new SelectQuery(
      root.table,
      root.st.*,
      root.rowFunc
    )
  }

  type Row = mysql.Row
  type Result = mysql.Result

  type SQLTable[Owner <: BaseTable[Owner, Record, Row], Record] = mysql.Table[Owner, Record]

  type Table[Owner <: BaseTable[Owner, Record, Row], Record] = mysql.Table[Owner, Record]

  def enumToQueryConditionPrimitive[T <: Enumeration](enum: T)(implicit ev: DataType[String]): DataType[T#Value] = {
    new DataType[T#Value] {

      override def sqlType: String = ev.sqlType

      override def deserialize(row: com.outworkers.morpheus.Row, name: String): Try[T#Value] = {
        row.string(name) map { s => enum.withName(s) }
      }

      override def serialize(value: T#Value): String = ev.serialize(value.toString)
    }
  }
}
