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
package com.outworkers.morpheus.dsl

import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.util.samplers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, FlatSpec}

class SQLBuiltQueryTest extends FlatSpec with Matchers {

  it should "serialise an append on an SQLBuiltQuery" in {
    val part1 = gen[ShortString].value
    val part2 = gen[ShortString].value

    val query = SQLBuiltQuery(part1).append(SQLBuiltQuery(part2)).queryString
    query shouldEqual s"$part1$part2"
  }

  it should "serialise a prepend on an SQLBuiltQuery" in {
    val part1 = gen[ShortString].value
    val part2 = gen[ShortString].value
    val query = SQLBuiltQuery(part1).prepend(SQLBuiltQuery(part2)).queryString
    query shouldEqual s"$part2$part1"
  }


  it should "serialise and pad an SQLBuiltQuery with a trailing space if the space is missing" in {
    val part1 = gen[ShortString].value
    val tested = part1.trim
    val query = SQLBuiltQuery(tested).pad.queryString
    query shouldEqual s"$tested "
  }

  it should "not add a trailing space if the last character of an SQLBuiltQuery is a space" in {
    val part1 = gen[ShortString].value
    val s = part1 + " "
    val query = SQLBuiltQuery(s).pad.queryString
    query shouldEqual s"$s"
  }

  it should "wrap a value in a set of parentheses" in {
    val part1 = gen[ShortString].value
    val value = gen[ShortString].value
    val query = SQLBuiltQuery(part1).wrap(value).queryString
    query shouldEqual s"$part1 ($value)"
  }

}
