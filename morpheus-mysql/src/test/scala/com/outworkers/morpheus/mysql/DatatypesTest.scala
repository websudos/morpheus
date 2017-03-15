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

import java.sql.{Date => SqlDate}
import java.util.Date

import com.outworkers.morpheus.builder.DefaultQueryBuilder
import com.outworkers.morpheus.mysql.dsl._
import com.outworkers.morpheus.{CustomSamplers, DataType, TimePrimitive}
import com.twitter.finagle.exp.mysql._
import org.joda.time.DateTime
import org.scalacheck.Arbitrary
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Assertion, FlatSpec, Matchers, TryValues}

class DatatypesTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks with CustomSamplers with TryValues {

  val helper = new TimePrimitive {}

  def defaultFn[T]: T => String = _.toString

  def escaped[T](obj: T): String = DefaultQueryBuilder.escapeValue(obj.toString)

  def dataTypeTest[T : DataType : Arbitrary](
    applier: T => Value,
    outcome: T => String = defaultFn[T]
  ): Assertion = {
    val dt = DataType[T]
    forAll { (obj: T, column: String) =>
      val value = applier(obj)
      val row = Row(new EmptyRow(_ => Some(value)))

      dt.serialize(obj) shouldEqual outcome(obj)
      dt.deserialize(row, column).success.value shouldEqual obj
    }
  }

  it should "parse a String from a row" in {
    dataTypeTest[String](StringValue.apply, escaped)
  }

  it should "parse an Int from a row" in {
    dataTypeTest[Int](IntValue.apply)
  }

  it should "parse a Long from a row" in {
    dataTypeTest[Long](LongValue.apply)
  }

  it should "parse a Double from a row" in {
    dataTypeTest[Double](DoubleValue.apply, escaped)
  }

  it should "parse a Float from a row" in {
    dataTypeTest[Float](FloatValue.apply, escaped)
  }

  it should "parse a Short from a row" in {
    dataTypeTest[Short](ShortValue.apply)
  }

  it should "parse a Date from a row" in {
    val dt = DataType[Date]

    forAll { (date: Date, column: String) =>
      val row = Row(new EmptyRow(_ => Some(DateValue(date.asSql))))

      dt.serialize(date) shouldEqual escaped(helper.javaDateFormat.format(date))
      dt.deserialize(row, column).success.value.toString shouldEqual date.toString
    }
  }

  ignore should "parse a DateTime from a row" in {
    val dt = DataType[DateTime]

    forAll { (date: DateTime, column: String) =>
      val row = Row(new EmptyRow(_ => Some(DateValue(date.asSql))))

      dt.serialize(date) shouldEqual escaped(date.toString(helper.jodaDateTimeFormat))
      dt.deserialize(row, column).success.value shouldEqual date
    }
  }

  ignore should "parse an SqlDate from a row" in {
    forAll { (date: SqlDate, column: String) =>
      val value = DateValue(date)
      val row = Row(new EmptyRow(_ => Some(value)))



      DataType[SqlDate].serialize(date) shouldEqual date.getTime.toString
      DataType[SqlDate].deserialize(row, column).success.value shouldEqual date
    }
  }
}
