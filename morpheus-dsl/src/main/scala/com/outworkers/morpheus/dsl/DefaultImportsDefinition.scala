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
package com.outworkers.morpheus.dsl

import com.outworkers.morpheus
import com.outworkers.morpheus.Row
import com.outworkers.morpheus.column.{AbstractColumn, DefaultForeignKeyConstraints}
import com.outworkers.morpheus.engine.query.AbstractQueryColumn

import scala.util.Try

/**
 * As the implementation of SQL builders may differ depending on the type of SQL database in use, we will provide a series of specific imports for each
 * individual database.
 *
 * For instance, for MySQL a user will {{ import com.outworkers.morpheus.mysql.dsl._ }}, for Postgress the user will {{import com.outworkers.morpheus
 * .postgres._ }} and so on. To make our life easy when we reach the point of writing disjoint import objects for the various SQL databases,
 * this trait will provide the base implementation of an "all you can eat" imports object.
 *
 * This includes the various conversions and implicit mechanisms which will have there own underlying structure. As any underlying variable implementation
 * between databases will still extend the same base implementations internally, we can easily override with the settings we need in any place we want.
 *
 * Thanks to Scala's ability to override the below type aliases, we can easily swap out the base BaseTable implementation for a MySQL specific BaseTable
 * implementation in a manner that's completely invisible to the API user. The naming of things can stay the same while morpheus invisibly implements all
 * necessary discrepancies.
 */
trait DefaultImportsDefinition extends DefaultForeignKeyConstraints {

  type DataType[T] = morpheus.DataType[T]

  object SQLPrimitive {
    def apply[T <: Enumeration](enum: T)(implicit ev: DataType[String]): DataType[T#Value] = {
      new DataType[T#Value] {

        override def sqlType: String = ev.sqlType

        override def deserialize(row: com.outworkers.morpheus.Row, name: String): Try[T#Value] = {
          row.string(name) map { s => enum.withName(s) }
        }

        override def serialize(value: T#Value): String = ev.serialize(value.toString)
      }
    }
  }

  type BaseTable[
    Owner <: BaseTable[Owner, Record, TableRow],
    Record,
    TableRow <: Row
  ] = com.outworkers.morpheus.dsl.BaseTable[Owner, Record, TableRow]

  type PrimaryKey = com.outworkers.morpheus.keys.PrimaryKey
  type UniqueKey = com.outworkers.morpheus.keys.UniqueKey
  type NotNull = com.outworkers.morpheus.keys.NotNull
  type Autoincrement = com.outworkers.morpheus.keys.Autoincrement
  type Zerofill = com.outworkers.morpheus.keys.Zerofill

  implicit def columnToQueryColumn[T : DataType](col: AbstractColumn[T]): AbstractQueryColumn[T]

}

