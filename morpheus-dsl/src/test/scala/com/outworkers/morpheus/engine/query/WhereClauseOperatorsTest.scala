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

import com.outworkers.morpheus.dsl.BasicTable
import org.scalatest.{FlatSpec, Matchers}
import com.outworkers.morpheus.sql._

class WhereClauseOperatorsTest extends FlatSpec with Matchers {
  it should "serialise a SELECT clause with a BETWEEN - AND operator sequence" in {
    BasicTable.select.where(_.count between 5 and 10).queryString shouldEqual "SELECT * FROM `BasicTable` WHERE count BETWEEN 5 AND 10;"
  }

  it should "serialise a SELECT clause with a BETWEEN - AND operator sequence inside an OR sequence" in {
    BasicTable.select
      .where(t => { (t.count between 5 and 10) or (t.count gte 5) })
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE (count BETWEEN 5 AND 10 OR count >= 5);"
  }

  it should "serialise a SELECT clause with a NOT BETWEEN - AND operator sequence" in {
    BasicTable.select.where(_.count notBetween 5 and 10)
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE count NOT BETWEEN 5 AND 10;"
  }

  it should "serialise a SELECT clause with a NOT BETWEEN - AND operator sequence inside an OR sequence" in {
    BasicTable.select
      .where(t => { (t.count notBetween 5 and 10) or (t.count gte 5) })
      .queryString shouldEqual "SELECT * FROM `BasicTable` WHERE (count NOT BETWEEN 5 AND 10 OR count >= 5);"
  }
}
