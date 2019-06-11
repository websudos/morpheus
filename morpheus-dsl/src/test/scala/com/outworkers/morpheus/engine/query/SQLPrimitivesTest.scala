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
package com.outworkers.morpheus.engine.query

import java.sql.Date

import com.outworkers.morpheus._
import com.outworkers.morpheus.builder.DefaultQueryBuilder
import com.outworkers.morpheus.helpers.TestRow
import com.outworkers.morpheus.sql._
import com.outworkers.util.samplers._
import org.joda.time.DateTime
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Success, Try}

class SQLPrimitivesTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks with CustomSamplers {

  "The SQL String primitive" should "always use '(apostrophes) around the serialised strings" in {
    val name = gen[ShortString].value
    val query = DataType[String].serialize(name)
    query shouldEqual s"'$name'"
  }

  "The SQL Long primitive" should "serialise a Long value to its string value" in {
    val value = gen[Long]
    val query = DataType[Long].serialize(value)
    query shouldEqual s"${value.toString}"
  }

  "The SQL Int primitive" should "serialise a Int value to its string value" in {
    val value = gen[Int]
    val query = DataType[Int].serialize(value)
    query shouldEqual s"${value.toString}"
  }

  it should "serialize and deserialize an Int" in {
    val primitive = new DefaultIntPrimitive

    forAll { value: Int =>

      val row = new TestRow {
        override def int(name: String): Try[Int] = Success(value)
      }

      primitive.serialize(value) shouldEqual value.toString

      primitive.deserialize(row, "") shouldEqual Success(value)
    }
  }

  it should "serialize and deserialize a Double" in {
    val primitive = new DefaultDoublePrimitive

    forAll { value: Double =>

      val row = new TestRow {
        override def double(name: String): Try[Double] = Success(value)
      }

      primitive.serialize(value) shouldEqual DefaultQueryBuilder.escapeValue(value.toString)

      primitive.deserialize(row, "") shouldEqual Success(value)
    }
  }

  it should "serialize and deserialize a Float" in {
    val primitive = new DefaultFloatPrimitive

    forAll { value: Float =>

      val row = new TestRow {
        override def float(name: String): Try[Float] = Success(value)
      }

      primitive.serialize(value) shouldEqual DefaultQueryBuilder.escapeValue(value.toString)

      primitive.deserialize(row, "") shouldEqual Success(value)
    }
  }

  it should "serialize and deserialize a Long" in {
    val primitive = new DefaultLongPrimitive

    forAll { value: Long =>

      val row = new TestRow {
        override def long(name: String): Try[Long] = Success(value)
      }

      primitive.serialize(value) shouldEqual value.toString

      primitive.deserialize(row, "") shouldEqual Success(value)
    }
  }

  it should "serialize and deserialize a Date" in {
    val primitive = new DefaultDatePrimitive

    forAll { value: Date =>

      val row = new TestRow {
        override def date(name: String): Try[Date] = Success(value)
      }

      primitive.serialize(value) shouldEqual DefaultQueryBuilder.escapeValue(primitive.javaDateFormat.format(value))

      primitive.deserialize(row, "") shouldEqual Success(value)
    }
  }

  it should "serialize and deserialize a DateTime" in {
    val ev = new DefaultDateTimePrimitive

    forAll { value: DateTime =>

      val row = new TestRow {
        override def datetime(name: String): Try[DateTime] = Success(value)
      }

      ev.serialize(value) shouldEqual DefaultQueryBuilder.escapeValue(value.toString(ev.jodaDateTimeFormat))

      ev.deserialize(row, "") shouldEqual Success(value)
    }
  }

  it should "serialize and deserialize a String" in {
    val primitive = new DefaultStringPrimitive

    forAll { value: String =>
      val row = new TestRow {
        override def string(name: String): Try[String] = Success(value)
      }

      primitive.serialize(value) shouldEqual DefaultQueryBuilder.escapeValue(value)

      primitive.deserialize(row, "") shouldEqual Success(value)
    }
  }
}

