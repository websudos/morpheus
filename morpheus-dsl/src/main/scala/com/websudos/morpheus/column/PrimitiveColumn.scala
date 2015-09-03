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

import java.util.Date

import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.{Row, SQLPrimitive}
import org.joda.time.DateTime

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
private[morpheus] class PrimitiveColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row, @specialized(Int, Double, Float, Long) RR : SQLPrimitive](t: BaseTable[T,
  R, TableRow]) extends Column[T, R, TableRow, RR](t) {

  def sqlType: String = implicitly[SQLPrimitive[RR]].sqlType

  def toQueryString(v: RR): String = implicitly[SQLPrimitive[RR]].toSQL(v)

  def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)

  def optional(r: Row): Try[RR] = implicitly[SQLPrimitive[RR]].fromRow(r, name)
}

class AbstractLongColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[Long])
  extends PrimitiveColumn[Owner, Record, TableRow, Long](table)

class AbstractDateColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[Date])
  extends PrimitiveColumn[Owner, Record, TableRow, Date](table)

class AbstractDateTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[DateTime])
  extends PrimitiveColumn[Owner, Record, TableRow, DateTime](table)

class AbstractDoubleTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[Double])
  extends PrimitiveColumn[Owner, Record, TableRow, Double](table)

class AbstractFloatTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[Float])
  extends PrimitiveColumn[Owner, Record, TableRow, Float](table)
