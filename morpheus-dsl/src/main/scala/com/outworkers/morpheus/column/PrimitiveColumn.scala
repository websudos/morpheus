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
package com.outworkers.morpheus.column

import java.util.Date

import com.outworkers.morpheus.DataType
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.Row
import org.joda.time.DateTime
import java.sql.{ Date => SqlDate }

import scala.annotation.implicitNotFound
import scala.util.Try

private[morpheus] object KnownTypeLimits {
  val varcharLimit = 65536
  val textLimit = 65536
  val mediumTextLimit = 65536
  val charLimit = 255
  val longTextLimit = 65536
}

@implicitNotFound(msg = "Type ${RR} must be a MySQL primitive")
private[morpheus] class PrimitiveColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row,
  @specialized(Int, Double, Float, Long) RR : DataType
](t: BaseTable[T, R, TableRow]) extends Column[T, R, TableRow, RR](t) {

  def sqlType: String = implicitly[DataType[RR]].sqlType

  def toQueryString(v: RR): String = implicitly[DataType[RR]].serialize(v)

  def qb: SQLBuiltQuery = SQLBuiltQuery.empty.appendEscape(name).pad.append(sqlType)

  def optional(r: Row): Try[RR] = implicitly[DataType[RR]].deserialize(r, name)
}

class AbstractLongColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[Long])
  extends PrimitiveColumn[Owner, Record, TableRow, Long](table)

class AbstractSqlDateColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[SqlDate])
  extends PrimitiveColumn[Owner, Record, TableRow, SqlDate](table)

class AbstractDateColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[Date])
  extends PrimitiveColumn[Owner, Record, TableRow, Date](table)

class AbstractDateTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[DateTime])
  extends PrimitiveColumn[Owner, Record, TableRow, DateTime](table)

class AbstractDoubleTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[Double])
  extends PrimitiveColumn[Owner, Record, TableRow, Double](table)

class AbstractFloatTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[Float])
  extends PrimitiveColumn[Owner, Record, TableRow, Float](table)
