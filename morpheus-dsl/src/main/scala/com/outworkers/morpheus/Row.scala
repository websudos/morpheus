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

import java.nio.ByteBuffer
import java.util.Date
import java.sql.{ Date => SqlDate, Timestamp => SqlTimestamp }

import com.twitter.util.Future
import org.joda.time.{DateTime, DateTimeZone}

import scala.util.Try

trait Row {

  def bool(name: String): Try[Boolean]

  def byte(name: String): Try[Byte]

  def string(name: String): Try[String]

  def byteBuffer(name: String): Try[ByteBuffer]

  def int(name: String): Try[Int]

  def double(name: String): Try[Double]

  def short(name: String): Try[Short]

  def date(name: String): Try[Date]

  def sqlDate(name: String): Try[SqlDate]

  def timestamp(name: String): Try[SqlTimestamp]

  def datetime(name: String): Try[DateTime] = timestamp(name) map {
    d => new DateTime(d.getTime, DateTimeZone.UTC)
  }

  def float(name: String): Try[Float]

  def long(name: String): Try[Long]

  def bigDecimal(name: String): Try[BigDecimal]
}

trait Result

trait Client[+DBRow, DBResult] {

  def select[T](query: String)(f: DBRow => T): Future[Seq[T]]

  def query(query: String): Future[DBResult]
}
