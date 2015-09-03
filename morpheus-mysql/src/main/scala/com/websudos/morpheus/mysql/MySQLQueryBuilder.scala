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

package com.websudos.morpheus.mysql

import java.util.Date

import com.twitter.finagle.exp.mysql.{Client => FinagleClient, Result => FinagleResult, ResultSet => FinagleResultSet, Row => FinagleRow, _}
import com.twitter.util.Future
import com.websudos.morpheus._
import com.websudos.morpheus.builder.{AbstractQueryBuilder, AbstractSQLSyntax, SQLOperatorSet}
import com.websudos.morpheus.column.AbstractColumn
import com.websudos.morpheus.query._

import com.websudos.morpheus.{ Row => BaseRow, Result => BaseResult }

case class MySQLResult(result: FinagleResult) extends BaseResult {}

case class MySQLRow(res: FinagleRow) extends BaseRow {

  override def string(name: String): String = {
    val extracted = res.apply(name)
    extracted match {
      case Some(StringValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def byte(name: String): Byte = {
    val extracted = res.apply(name)
    extracted match {
      case Some(ByteValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def int(name: String): Int = {
    val extracted = res.apply(name)
    extracted match {
      case Some(IntValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def date(name: String): Date = {
    val extracted = res.apply(name)
    extracted match {
      case Some(DateValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def bigDecimal(name: String): BigDecimal = {
    val extracted = res.apply(name)
    extracted match {
      case Some(BigDecimalValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def double(name: String): Double = {
    val extracted = res.apply(name)
    extracted match {
      case Some(DoubleValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def float(name: String): Float = {
    val extracted = res.apply(name)
    extracted match {
      case Some(FloatValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def long(name: String): Long = {
    val extracted = res.apply(name)
    extracted match {
      case Some(LongValue(value)) => value
      case Some(StringValue(value)) => value.toLong
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def short(name: String): Short = {
    val extracted = res.apply(name)
    extracted match {
      case Some(ShortValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

}

class MySQLClient(val client: FinagleClient) extends Client[MySQLRow, MySQLResult] {

  def select[T](qb: String)(f: MySQLRow => T): Future[Seq[T]] = {
    // logger.info(s"Executing query $qb")
    client.query(qb).map {
      case set: FinagleResultSet => set.rows.map {
        row => f(new MySQLRow(row))
      }
      case _ => Seq.empty[T]
    }
  }

  def query(query: String): Future[MySQLResult] = {
    client.query(query).map { res => MySQLResult(res) }
  }

}

object MySQLSyntax extends AbstractSQLSyntax {


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


object MySQLOperatorSet extends SQLOperatorSet

object MySQLQueryBuilder extends AbstractQueryBuilder {
  val operators = MySQLOperatorSet
  val syntax = MySQLSyntax
}

private[morpheus] class MySQLQueryColumn[T : SQLPrimitive](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)
