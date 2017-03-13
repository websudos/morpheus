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
package com.outworkers.morpheus.helpers

import java.nio.ByteBuffer
import java.util.Date
import java.sql.{Timestamp, Date => SqlDate}

import com.outworkers.morpheus.Row

import scala.util.control.NoStackTrace
import scala.util.{Failure, Try}

class TestRow extends Row {

  def failWith[T](msg: String): Failure[T] = Failure(new RuntimeException(msg) with NoStackTrace)

  override def bool(name: String): Try[Boolean] = failWith("Bool extraction not implemented")

  override def byte(name: String): Try[Byte] = failWith("Byte extraction not implemented")

  override def string(name: String): Try[String] = failWith("String extraction not implemented")

  override def byteBuffer(name: String): Try[ByteBuffer] = failWith("ByteBuffer extraction not implemented")

  override def int(name: String): Try[Int] = failWith("Int extraction not implemented")

  override def double(name: String): Try[Double] = failWith("Double extraction not implemented")

  override def short(name: String): Try[Short] = failWith("Short extraction not implemented")

  override def date(name: String): Try[Date] = failWith("Date extraction not implemented")

  override def sqlDate(name: String): Try[SqlDate] = failWith("Date extraction not implemented")

  override def float(name: String): Try[Float] = failWith("Float extraction not implemented")

  override def long(name: String): Try[Long] = failWith("Long extraction not implemented")

  override def bigDecimal(name: String): Try[BigDecimal] = failWith("BigDecimal extraction not implemented")

  override def timestamp(name: String): Try[Timestamp] = failWith("Timestamp extraction not implemented")
}
