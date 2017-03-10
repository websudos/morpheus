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
package mysql

import java.nio.ByteBuffer
import java.util.Date

import com.outworkers.morpheus.builder.{AbstractQueryBuilder, AbstractSQLSyntax, SQLOperatorSet}
import com.outworkers.morpheus.column.AbstractColumn
import com.outworkers.morpheus.engine.query.AbstractQueryColumn
import com.outworkers.morpheus.{Client => RootClient, Result => BaseResult, Row => BaseRow}
import com.twitter.finagle.exp.mysql.{Client => FinagleClient, Result => FinagleResult, ResultSet => FinagleResultSet, Row => FinagleRow, _}
import com.twitter.finagle.exp.{mysql => fsql}
import com.twitter.util.Future

import scala.util.{Failure, Success, Try}

case class Result(result: FinagleResult) extends BaseResult

case class Row(res: FinagleRow) extends BaseRow {

  protected[this] def extract[T](column: String)(fn: Option[Value] => Try[T]): Try[T] = {
    fn(res(column))
  }

  override def string(name: String): Try[String] = {
    extract(name) {
      case Some(StringValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected string, got $x"))
    }
  }

  override def byte(name: String): Try[Byte] = {
    extract(name) {
      case Some(ByteValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected byte, got $x"))
    }
  }

  override def int(name: String): Try[Int] = {
    extract(name) {
      case Some(IntValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected int, got $x"))
    }
  }

  override def date(name: String): Try[Date] = {
    extract(name) {
      case Some(DateValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected int, got $x"))
    }
  }

  override def bigDecimal(name: String): Try[BigDecimal] = {
    extract(name) {
      case Some(BigDecimalValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected BigDecimal, got $x"))
    }
  }

  override def double(name: String): Try[Double] = {
    extract(name) {
      case Some(DoubleValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected double, got $x"))
    }
  }

  override def float(name: String): Try[Float] = {
    extract(name) {
      case Some(FloatValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected float, got $x"))
    }
  }

  override def long(name: String): Try[Long] = {
    extract(name) {
      case Some(LongValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected long, got $x"))
    }
  }

  override def short(name: String): Try[Short] = {
    extract(name) {
      case Some(ShortValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected short, got $x"))
    }
  }

  override def bool(name: String): Try[Boolean] = {
    extract(name) {
      case Some(fsql.StringValue("true")) => Success(true)
      case Some(fsql.StringValue("false")) => Success(false)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected boolean, got $x"))
    }
  }

  override def byteBuffer(name: String): Try[ByteBuffer] = {
    extract(name) {
      case Some(fsql.RawValue(typ, charset, isBinary, bytes)) => Success(ByteBuffer.wrap(bytes))
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected ByteArray, got $x"))
    }
  }
}

class Client(val client: FinagleClient) extends RootClient[Row, Result] {

  def select[T](qb: String)(f: mysql.Row => T): Future[Seq[T]] = {
    client.query(qb).map {
      case res: FinagleResultSet => res.rows.map {
        row => f(mysql.Row(row))
      }
      case _ => Seq.empty[T]
    }
  }

  def query(query: String): Future[Result] = {
    client.query(query).map { res => Result(res) }
  }
}

object Syntax extends AbstractSQLSyntax {

  object SelectOptions {
    val distinctRow = "DISTINCTROW"
    val straightJoin = "STRAIGHT_JOIN"
    val sqlSmallResult = "SQL_SMALL_RESULT"
    val sqlBigResult = "SQL_BIG_RESULT"
    val sqlBufferResult = "SQL_BUFFER_RESULT"
    val sqlCache = "SQL_CACHE"
    val sqlNoCache = "SQL_NO_CACHE"
    val sqlCalcFoundRows = "SQL_CALC_FOUND_ROWS"
  }

  object Priorities {
    val lowPriority = "LOW_PRIORITY"
    val highPriority = "HIGH_PRIORITY"
  }

  object DeleteOptions {
    val ignore = "IGNORE"
    val quick = "QUICK"
  }

  object InsertOptions {
    val ignore = "IGNORE"
    val delayed = "DELAYED"
  }
}


object OperatorSet extends SQLOperatorSet

object QueryBuilder extends AbstractQueryBuilder {
  val operators = OperatorSet
  val syntax = Syntax
}

private[morpheus] class QueryColumn[T : DataType](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)
