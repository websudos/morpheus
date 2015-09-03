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

package com.websudos.morpheus.column

import com.websudos.morpheus.builder.{SQLBuiltQuery, DefaultSQLDataTypes}
import com.websudos.morpheus.{Row, SQLPrimitive}
import com.websudos.morpheus.dsl.BaseTable

sealed abstract class NumericColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row, ValueType : Numeric : SQLPrimitive](t: BaseTable[T, R, TableRow], limit: Int =
0) extends PrimitiveColumn[T, R, TableRow, ValueType](t) {

  protected[this] def numericType: String

  override def sqlType: String = if (limit > 0) s"$numericType($limit)" else numericType

  override def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)

  def unsigned: Boolean = false
}

abstract class AbstractTinyIntColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev:
SQLPrimitive[Int])
  extends NumericColumn[T, R, TableRow, Int](t, limit) {

  override protected[this] val numericType: String = DefaultSQLDataTypes.tinyInt
}

abstract class AbstractSmallIntColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev:
SQLPrimitive[Int])
extends NumericColumn[T, R, TableRow, Int](t, limit) {

  override protected[this] val numericType: String = DefaultSQLDataTypes.smallInt
}

abstract class AbstractMediumIntColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev:
SQLPrimitive[Int])
  extends NumericColumn[T, R, TableRow, Int](t, limit) {

  override protected[this] val numericType: String = DefaultSQLDataTypes.mediumInt
}

abstract class AbstractIntColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev:
SQLPrimitive[Int])
  extends NumericColumn[T, R, TableRow, Int](t, limit) {
  override protected[this] val numericType: String = DefaultSQLDataTypes.int
}

abstract class AbstractYearColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow])(implicit ev: SQLPrimitive[Int])
  extends PrimitiveColumn[T, R, TableRow, Int](t) {
  override val sqlType = DefaultSQLDataTypes.year
}


