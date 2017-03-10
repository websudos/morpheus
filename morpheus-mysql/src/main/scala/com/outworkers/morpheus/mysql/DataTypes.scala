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
package com.outworkers.morpheus.mysql

import java.util.Date

import com.outworkers.morpheus._
import com.outworkers.morpheus._
import org.joda.time.DateTime

import scala.util.Try

object DataTypes {
  object Real {
    val float = "FLOAT"
    val double = "DOUBLE"
    val decimal = "REAL"
  }
}

trait DataTypes {

  def apply[RR](implicit ev: DataType[RR]): DataType[RR] = ev

  implicit object IntPrimitive extends DefaultIntPrimitive {
    def fromRow(row: Row, name: String): Try[Int] = row.int(name)
  }

  implicit object FloatPrimitive extends DefaultFloatPrimitive {
    def fromRow(row: Row, name: String): Try[Float] = row.float(name)
  }

  implicit object DoublePrimitive extends DefaultDoublePrimitive {
    def fromRow(row: Row, name: String): Try[Double] = row.double(name)
  }

  implicit object LongPrimitive extends DefaultLongPrimitive {
    def fromRow(row: Row, name: String): Try[Long] = row.long(name)
  }

  implicit object DatePrimitive extends DefaultDatePrimitive {
    def fromRow(row: Row, name: String): Try[Date] = row.date(name)
  }

  implicit object DateTimePrimitive extends DefaultDateTimePrimitive {
    def fromRow(row: Row, name: String): Try[DateTime] = row.datetime(name)
  }

  implicit object StringPrimitive extends DefaultStringPrimitive {
    def fromRow(row: Row, name: String): Try[String] = row.string(name)
  }
}
