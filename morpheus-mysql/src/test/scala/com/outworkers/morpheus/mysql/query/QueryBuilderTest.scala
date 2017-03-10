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
package com.outworkers.morpheus.mysql.query

import com.outworkers.morpheus.mysql.QueryBuilder
import com.outworkers.morpheus.builder.SQLBuiltQuery
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import com.outworkers.util.testing._

class QueryBuilderTest extends FlatSpec with Matchers {

  it should "serialise a simple equals condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.eqs(name, value).queryString
    query shouldEqual s"$name = $value"
  }

  it should "serialise a simple lt condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.lt(name, value).queryString
    query shouldEqual s"$name < $value"
  }

  it should "serialise a simple lte condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.lte(name, value).queryString
    query shouldEqual s"$name <= $value"
  }

  it should "serialise a simple gt condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.gt(name, value).queryString
    query shouldEqual s"$name > $value"
  }

  it should "serialise a simple gte condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.gte(name, value).queryString
    query shouldEqual s"$name >= $value"
  }

  it should "serialise a simple != condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.!=(name, value).queryString
    query shouldEqual s"$name != $value"
  }

  it should "serialise a simple <> condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.<>(name, value).queryString
    query shouldEqual s"$name <> $value"
  }

  it should "serialise a simple <=> condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.<=>(name, value).queryString
    query shouldEqual s"$name <=> $value"
  }

  it should "serialise a simple select * query" in {
    val name = gen[String]
    val query = QueryBuilder.select(name).queryString
    query shouldEqual s"SELECT * FROM `$name`"
  }

  it should "serialise a partial select query where 1 column name is specified" in {
    val name = gen[String]
    val column = gen[ShortString].value
    val query = QueryBuilder.select(name, column).queryString
    query shouldEqual s"SELECT $column FROM `$name`"
  }

  it should "serialise a partial select query where 2 column names are specified" in {
    val name = gen[String]
    val column1 = gen[ShortString].value
    val column2 = gen[ShortString].value
    val query = QueryBuilder.select(name, column1, column2).queryString
    query shouldEqual s"SELECT $column1 $column2 FROM `$name`"
  }

  it should "serialise a partial select query where 3 column names are specified" in {
    val name = gen[String]
    val column1 = gen[ShortString].value
    val column2 = gen[ShortString].value
    val column3 = gen[ShortString].value

    val query = QueryBuilder.select(name, column1, column2, column3).queryString
    query shouldEqual s"SELECT $column1 $column2 $column3 FROM `$name`"
  }

  it should "serialise an IN operator query" in {
    val name = gen[String]
    val column1 = gen[ShortString].value
    val column2 = gen[ShortString].value
    val column3 = gen[ShortString].value

    val query = QueryBuilder.in(name, List(column1, column2, column3)).queryString
    query shouldEqual s"$name IN ($column1, $column2, $column3)"
  }

  it should "serialise an NOT IN operator query" in {
    val name = gen[String]
    val column1 = gen[ShortString].value
    val column2 = gen[ShortString].value
    val column3 = gen[ShortString].value
    val query = QueryBuilder.notIn(name, List(column1, column2, column3)).queryString
    query shouldEqual s"$name NOT IN ($column1, $column2, $column3)"
  }

  it should "serialise an setTo operator query" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.setTo(name, value).queryString
    query shouldEqual s"$name = $value"
  }


  it should "serialise an ASC operator query" in {
    val name = gen[String]
    val query = QueryBuilder.asc(name).queryString
    query shouldEqual s"$name ASC"
  }

  it should "serialise a DESC operator query" in {
    val name = gen[String]
    val value = gen[String]
    val query = QueryBuilder.desc(name).queryString
    query shouldEqual s"$name DESC"
  }

  it should "serialise a SET query" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val query = QueryBuilder.set(SQLBuiltQuery(part), SQLBuiltQuery(name)).queryString
    query shouldEqual s"$part SET $name"
  }

  it should "serialise a LIKE operator query" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val tested = part.trim
    val query = QueryBuilder.like(tested, name).queryString
    query shouldEqual s"$tested LIKE $name"
  }

  it should "serialise a NOT LIKE operator query" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val query = QueryBuilder.notLike(part, name).queryString
    query shouldEqual s"$part NOT LIKE $name"
  }

  it should "correctly set multiple conditions" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val query = QueryBuilder.andSet(SQLBuiltQuery(part), SQLBuiltQuery(name)).queryString
    query shouldEqual s"$part, $name"
  }

  it should "append an ENGINE clause" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val query = QueryBuilder.engine(SQLBuiltQuery(part), name).queryString
    query shouldEqual s"$part ENGINE $name"
  }

  it should "serialise a BIN clause" in {
    val part = gen[ShortString].value
    val query = QueryBuilder.bin(part).queryString
    query shouldEqual s"BIN ($part)"
  }

  it should "serialise a CHAR_LENGTH clause" in {
    val part = gen[ShortString].value
    val query = QueryBuilder.charLength(part).queryString
    query shouldEqual s"CHAR_LENGTH ($part)"
  }

  it should "serialise a CHARACTER_LENGTH clause" in {
    val part = gen[ShortString].value
    val query = QueryBuilder.characterLength(part).queryString
    query shouldEqual s"CHARACTER_LENGTH ($part)"
  }

  it should "serialise a BIT_LENGTH clause" in {
    val part = gen[ShortString].value
    val query = QueryBuilder.bitLength(part).queryString
    query shouldEqual s"BIT_LENGTH ($part)"
  }

  it should "serialise a ASCII clause" in {
    val part = gen[ShortString].value
    val query = QueryBuilder.ascii(part).queryString
    query shouldEqual s"ASCII ($part)"
  }

  it should "serialise an EXISTS clause" in {
    val part = gen[ShortString].value
    val query = QueryBuilder.exists(SQLBuiltQuery(part)).queryString
    query shouldEqual s"EXISTS ($part)"
  }

  it should "serialise a NOT EXISTS clause" in {
    val part = gen[ShortString].value
    val query = QueryBuilder.notExists(SQLBuiltQuery(part)).queryString
    query shouldEqual s"NOT EXISTS ($part)"
  }

  it should "serialise a ON clause" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val value = gen[ShortString].value
    val query = QueryBuilder.on(SQLBuiltQuery(part), SQLBuiltQuery(value)).queryString
    query shouldEqual s"$part ON $value"
  }

  it should "serialise a BETWEEN clause" in {
    val part = gen[ShortString].value
    val value = gen[ShortString].value
    val query = QueryBuilder.between(part, value).queryString
    query shouldEqual s"$part BETWEEN $value"
  }

  it should "serialise a NOT BETWEEN clause" in {
    val part = gen[ShortString].value
    val value = gen[ShortString].value
    val query = QueryBuilder.notBetween(part, value).queryString
    query shouldEqual s"$part NOT BETWEEN $value"
  }
}
