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
package com.websudos.morpheus

import java.util.Date
import org.joda.time.DateTime

import com.websudos.morpheus.builder.{DefaultQueryBuilder, DefaultSQLDataTypes}

import scala.util.Try

case class InvalidTypeDefinitionException(msg: String = "Invalid SQL type declared for column") extends RuntimeException(msg)


/**
 * The innocent reader may now try and ask the question "What does this do?".
 * The type class approach is meant to limit the amount of Data types we inherently recognise as defaults or primitives.
 *
 * This allows for a very simple limitation mechanism where we exploit the basic Scala implicit resolution mechanism to perform efficient type restrictions
 * and encoding/decoding to/from database results.
 * If a type needs serialisation, we simply request for type evidence via the below type class using {@code}implicity[SQLPrimitive[T]]{code}.
 *
 * If proof exists that the type T has an SQLPrimitive type class associated with it, then the type is considered primitive with regards to the current SQL
 * database in use. This means we will allow for variation of primitive type implementations between different SQL databases. The challenge arises from
 * needing to parse Primitives implicitly in the DSL module itself to create the basic extendable functionality.
 *
 * We then need to allow other database specific implementations to override these implementations and pass the new ones explicity. As this
 * encapsulates very important information, such as how to decode a specific type from a given database implementation when a query is performed,
 * the breach of this contract means we might as well start another project.
 *
 * Any below weirdness or poor choice of semantics is probably the only possible way through which the above was achieved
 * This involves defining the default extendable types as normal classes instead of traits to allow their instantiation as overridable vals.
 *
 * @tparam T The primitive SQL data type to create a type class for.
 */
trait SQLPrimitive[T] {

  def sqlType: String

  def toSQL(value: T): String

  def fromRow(row: Row, name: String): Try[T]
}

class DefaultIntPrimitive extends SQLPrimitive[Int] {
  override def sqlType: String = DefaultSQLDataTypes.int

  override def toSQL(value: Int): String = {
    value.toString
  }

  def fromRow(row: Row, name: String): Try[Int] = Try(row.int(name))
}

class DefaultFloatPrimitive extends SQLPrimitive[Float] {
  override def sqlType: String = DefaultSQLDataTypes.float

  override def toSQL(value: Float): String = value.toString

  def fromRow(row: Row, name: String): Try[Float] = Try(row.float(name))
}

class DefaultDoublePrimitive extends SQLPrimitive[Double] {
  override def sqlType: String = DefaultSQLDataTypes.double

  override def toSQL(value: Double): String = value.toString

  def fromRow(row: Row, name: String): Try[Double] = Try(row.double(name))
}

class DefaultLongPrimitive extends SQLPrimitive[Long] {
  override def sqlType: String = DefaultSQLDataTypes.long

  override def toSQL(value: Long): String = value.toString

  def fromRow(row: Row, name: String): Try[Long] = Try(row.long(name))
}

class DefaultDatePrimitive extends SQLPrimitive[Date] {
  def sqlType: String = DefaultSQLDataTypes.date

  def toSQL(value: Date): String = value.toString

  def fromRow(row: Row, name: String): Try[Date] = Try(row.date(name))
}

class DefaultDateTimePrimitive extends SQLPrimitive[DateTime] {
  def sqlType: String = DefaultSQLDataTypes.dateTime

  def toSQL(value: DateTime): String = value.toString

  def fromRow(row: Row, name: String): Try[DateTime] = Try(row.datetime(name))
}

class DefaultStringPrimitive extends SQLPrimitive[String] {

  override def sqlType: String = DefaultSQLDataTypes.text

  override def toSQL(value: String): String = DefaultQueryBuilder.escape(value)

  def fromRow(row: Row, name: String): Try[String] = {
    Try(row.string(name))
  }
}

trait MaterialisedPrimitives {
  implicit case object IntPrimitive extends DefaultIntPrimitive

  implicit case object StringPrimitive extends DefaultStringPrimitive

  implicit case object FloatPrimitive extends DefaultFloatPrimitive

  implicit case object DatePrimitive extends DefaultDatePrimitive

  implicit case object DateTimePrimitive extends DefaultDateTimePrimitive

  implicit case object DoublePrimitive extends DefaultDoublePrimitive

  implicit case object LongPrimitive extends DefaultLongPrimitive
}
