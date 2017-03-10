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

import com.outworkers.util.testing._
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.outworkers.morpheus.sql._

class SQLPrimitivesTest extends FlatSpec with Matchers {

  "The SQL String primitive" should "always use '(apostrophes) around the serialised strings" in {
    val name = gen[ShortString].value
    val query = implicitly[DataType[String]].serialize(name)
    query shouldEqual s"'$name'"
  }

  "The SQL Long primitive" should "serialise a Long value to its string value" in {
    val value = gen[Long]
    val query = implicitly[DataType[Long]].serialize(value)
    query shouldEqual s"${value.toString}"
  }

  "The SQL Int primitive" should "serialise a Int value to its string value" in {
    val value = gen[Int]
    val query = implicitly[DataType[Int]].serialize(value)
    query shouldEqual s"${value.toString}"
  }


}
