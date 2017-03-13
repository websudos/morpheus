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
import com.outworkers.morpheus.builder.{DefaultSQLDataTypes, SQLBuiltQuery}
import com.outworkers.morpheus.dsl.BaseTable

private[morpheus] abstract class NumericColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row,
  ValueType : Numeric : DataType
](t: BaseTable[T, R, TableRow], limit: Int = 0) extends PrimitiveColumn[T, R, TableRow, ValueType](t) {

  protected[this] def numericType: String

  override def sqlType: String = if (limit > 0) s"$numericType($limit)" else numericType

  def unsigned: Boolean = false
}

abstract class AbstractTinyIntColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[Int]
) extends NumericColumn[T, R, TableRow, Int](t, limit) {
  override protected[this] val numericType: String = DefaultSQLDataTypes.tinyInt
}

abstract class AbstractSmallIntColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[Int]
) extends NumericColumn[T, R, TableRow, Int](t, limit) {
  override protected[this] val numericType: String = DefaultSQLDataTypes.smallInt
}

abstract class AbstractMediumIntColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[Int]
) extends NumericColumn[T, R, TableRow, Int](t, limit) {
  override protected[this] val numericType: String = DefaultSQLDataTypes.mediumInt
}

abstract class AbstractIntColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow], limit: Int = 0)(
  implicit ev: DataType[Int]
) extends NumericColumn[T, R, TableRow, Int](t, limit) {
  override protected[this] val numericType: String = DefaultSQLDataTypes.int
}

abstract class AbstractYearColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row
](t: BaseTable[T, R, TableRow])(implicit ev: DataType[Int]) extends PrimitiveColumn[T, R, TableRow, Int](t) {
  override val sqlType = DefaultSQLDataTypes.year
}


