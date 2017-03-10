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

import com.outworkers.morpheus.{Row, DataType}
import com.outworkers.morpheus.builder.{DefaultQueryBuilder, DefaultSQLDataTypes, SQLBuiltQuery}
import com.outworkers.morpheus.dsl.BaseTable

import scala.util.{Failure, Success, Try}

abstract class LimitedTextColumn[
  T <: BaseTable[T, R, TableRow],
  R, TableRow <: Row,
  RR : DataType
](t: BaseTable[T, R, TableRow], protected[this] val limit: Int) extends PrimitiveColumn[T, R, TableRow, RR](t)

abstract class UnlimitedTextColumn[
  T <: BaseTable[T, R, TableRow],
  R, TableRow <: Row,
  RR : DataType
](t: BaseTable[T, R, TableRow]) extends PrimitiveColumn[T, R, TableRow, RR](t)


abstract class AbstractCharColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = KnownTypeLimits.charLimit)
  (implicit ev: DataType[String]) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {

  override def sqlType: String = s"${DefaultSQLDataTypes.char}($limit)"
}

abstract class AbstractVarcharColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = KnownTypeLimits.varcharLimit)(
  implicit ev: DataType[String]
) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = s"${DefaultSQLDataTypes.varchar}($limit)"
}

abstract class AbstractTinyTextColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[String]
) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.tinyText
}

abstract class AbstractTextColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[String]
) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.text
}

abstract class AbstractMediumTextColumn[
  T <: BaseTable[T, R, TableRow],
  R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[String]
) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.mediumText
}

abstract class AbstractLongTextColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[String]) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.longText
}

abstract class AbstractTinyBlobColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[String]
) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.tinyBlob
}

abstract class AbstractBlobColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[String]
) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.blob
}

abstract class AbstractMediumBlobColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[String]
) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.mediumBlob
}

abstract class AbstractLongBlobColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[String]
) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.longBlob
}

abstract class AbstractEnumColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row,
  EnumType <: Enumeration
](table: BaseTable[Owner, Record, TableRow], enum: EnumType)
  extends Column[Owner, Record, TableRow, EnumType#Value](table) {

  override def optional(r: Row): Try[EnumType#Value] = {
    r.string(name) flatMap (s => {
      enum.values.find(_.toString == s) match {
        case Some(value) => Success(value)
        case None => Failure(new Exception(s"Enumeration $enum doesn't contain value $s"))
      }
    })
  }

  override def toQueryString(v: EnumType#Value): String = DefaultQueryBuilder.escape(v.toString)

  override def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)

  override def sqlType: String = s"${DefaultSQLDataTypes.varchar}(200)"
}

abstract class AbstractOptionalEnumColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row,
  EnumType <: Enumeration
](
  table: BaseTable[Owner, Record, TableRow],
  enum: EnumType
) extends OptionalColumn[Owner, Record, TableRow, String](table) {

  override def sqlType: String = DefaultSQLDataTypes.varchar
}
