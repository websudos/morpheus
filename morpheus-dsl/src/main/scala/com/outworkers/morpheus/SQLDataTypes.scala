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

import java.sql.{Date => SqlDate, Timestamp => SqlTimestamp}
import java.text.SimpleDateFormat
import java.util.Date

import com.outworkers.morpheus.builder.{DefaultQueryBuilder, DefaultSQLDataTypes, SQLBuiltQuery}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.util.Try
import scala.util.control.NoStackTrace

case class InvalidTypeDefinitionException(
  msg: String = "Invalid SQL type declared for column"
) extends RuntimeException(msg) with NoStackTrace


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
trait DataType[T] {

  def sqlType: String

  def serialize(value: T): String

  def deserialize(row: Row, name: String): Try[T]
}

object DataType {
  def apply[T]()(implicit ev: DataType[T]): DataType[T] = ev
}

class DefaultIntPrimitive extends DataType[Int] {
  override def sqlType: String = DefaultSQLDataTypes.int

  override def serialize(value: Int): String = value.toString

  def deserialize(row: Row, name: String): Try[Int] = row.int(name)
}

class DefaultShortPrimitive extends DataType[Short] {
  override def sqlType: String = DefaultSQLDataTypes.short

  override def serialize(value: Short): String = value.toString

  def deserialize(row: Row, name: String): Try[Short] = row.short(name)
}

class DefaultFloatPrimitive extends DataType[Float] {
  override def sqlType: String = DefaultSQLDataTypes.float

  override def serialize(value: Float): String = DefaultQueryBuilder.escapeValue(value.toString)

  def deserialize(row: Row, name: String): Try[Float] = row.float(name)
}

class DefaultDoublePrimitive extends DataType[Double] {
  override def sqlType: String = DefaultSQLDataTypes.double

  override def serialize(value: Double): String = DefaultQueryBuilder.escapeValue(value.toString)

  def deserialize(row: Row, name: String): Try[Double] = row.double(name)
}

private[morpheus] trait TimePrimitive {
  protected[morpheus] val mysqlDatePattern = "yyyy-MM-dd"
  protected[morpheus] val javaDateFormat = new SimpleDateFormat(mysqlDatePattern)
  protected[morpheus] val jodaDateFormat = DateTimeFormat.forPattern(mysqlDatePattern)
  protected[morpheus] val jodaDateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
}

class DefaultLongPrimitive extends DataType[Long] {
  override def sqlType: String = DefaultSQLDataTypes.long

  override def serialize(value: Long): String = value.toString

  def deserialize(row: Row, name: String): Try[Long] = row.long(name)
}

class DefaultSqlDatePrimitive extends DataType[SqlDate] with TimePrimitive {
  def sqlType: String = DefaultSQLDataTypes.date

  def serialize(value: SqlDate): String = javaDateFormat.format(new Date(value.getTime))

  def deserialize(row: Row, name: String): Try[SqlDate] = row.sqlDate(name)
}

class DefaultDatePrimitive extends DataType[Date] with TimePrimitive {

  def sqlType: String = DefaultSQLDataTypes.date

  def serialize(value: Date): String = DefaultQueryBuilder.escapeValue(javaDateFormat.format(value))

  def deserialize(row: Row, name: String): Try[Date] = row.date(name)
}

class DefaultTimestampPrimitive extends DataType[SqlTimestamp] with TimePrimitive {
  def sqlType: String = DefaultSQLDataTypes.dateTime

  def serialize(value: SqlTimestamp): String = value.getTime.toString

  def deserialize(row: Row, name: String): Try[SqlTimestamp] = row.timestamp(name)
}


class DefaultDateTimePrimitive extends DataType[DateTime] with TimePrimitive {
  def sqlType: String = DefaultSQLDataTypes.timestamp

  def serialize(value: DateTime): String = DefaultQueryBuilder.escapeValue(value.toString(jodaDateTimeFormat))

  def deserialize(row: Row, name: String): Try[DateTime] = row.datetime(name)
}

class DefaultStringPrimitive extends DataType[String] {

  override def sqlType: String = DefaultSQLDataTypes.text

  override def serialize(value: String): String = DefaultQueryBuilder.escapeValue(value)

  def deserialize(row: Row, name: String): Try[String] = row.string(name)
}

trait DefaultDataTypes {
  implicit object IntPrimitive extends DefaultIntPrimitive

  implicit object StringPrimitive extends DefaultStringPrimitive

  implicit object FloatPrimitive extends DefaultFloatPrimitive

  implicit object SqlDatePrimitive extends DefaultSqlDatePrimitive

  implicit object DatePrimitive extends DefaultDatePrimitive

  implicit object TimestampPrimitive extends DefaultTimestampPrimitive

  implicit object DateTimePrimitive extends DefaultDateTimePrimitive

  implicit object DoublePrimitive extends DefaultDoublePrimitive

  implicit object LongPrimitive extends DefaultLongPrimitive
}
