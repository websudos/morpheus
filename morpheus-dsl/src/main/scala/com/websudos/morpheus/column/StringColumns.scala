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

import com.websudos.morpheus.builder.{DefaultQueryBuilder, DefaultSQLDataTypes, SQLBuiltQuery}
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.{Row, SQLPrimitive}

import scala.util.{Failure, Success, Try}

abstract class LimitedTextColumn[
  T <: BaseTable[T, R, TableRow],
  R, TableRow <: Row,
  RR : SQLPrimitive
](t: BaseTable[T, R, TableRow], protected[this] val limit: Int) extends PrimitiveColumn[T, R, TableRow, RR](t)

abstract class UnlimitedTextColumn[
  T <: BaseTable[T, R, TableRow],
  R, TableRow <: Row,
  RR : SQLPrimitive
](t: BaseTable[T, R, TableRow]) extends PrimitiveColumn[T, R, TableRow, RR](t)


abstract class AbstractCharColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = KnownTypeLimits.charLimit)
  (implicit ev: SQLPrimitive[String]) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {

  override def sqlType: String = s"${DefaultSQLDataTypes.char}($limit)"
}

abstract class AbstractVarcharColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = KnownTypeLimits
  .varcharLimit)(implicit ev: SQLPrimitive[String]) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = s"${DefaultSQLDataTypes.varchar}($limit)"
}

abstract class AbstractTinyTextColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.tinyText
}

abstract class AbstractTextColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.text
}

abstract class AbstractMediumTextColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.mediumText
}

abstract class AbstractLongTextColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.longText
}

abstract class AbstractTinyBlobColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.tinyBlob
}

abstract class AbstractBlobColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.blob
}

abstract class AbstractMediumBlobColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.mediumBlob
}

abstract class AbstractLongBlobColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.longBlob
}




abstract class AbstractEnumColumn[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: Row, EnumType <: Enumeration](table: BaseTable[Owner, Record, TableRow], enum: EnumType)
  extends Column[Owner, Record, TableRow, EnumType#Value](table) {

  override def optional(r: Row): Try[EnumType#Value] = {
    val enumConstant = r.string(name)

    enum.values.find(_.toString == enumConstant) match {
      case Some(value) => Success(value)
      case None => Failure(new Exception(s"Enumeration ${enum.toString()} doesn't contain value $enumConstant"))
    }
  }

  override def toQueryString(v: EnumType#Value): String = DefaultQueryBuilder.escape(v.toString)

  override def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)

  override def sqlType: String = s"${DefaultSQLDataTypes.varchar}(200)"
}

abstract class AbstractOptionalEnumColumn[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: Row, EnumType <: Enumeration](table: BaseTable[Owner, Record, TableRow], enum: EnumType)
  extends OptionalColumn[Owner, Record, TableRow, String](table) {

  override def sqlType: String = DefaultSQLDataTypes.varchar
}
