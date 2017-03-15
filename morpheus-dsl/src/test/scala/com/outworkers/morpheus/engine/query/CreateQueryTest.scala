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

class CreateQueryTest extends FlatSpec with Matchers {

  it should "serialise a simple CREATE query" in {
    BasicTable.create.queryString shouldEqual "CREATE TABLE `BasicTable`;"
  }

  it should "serialise a simple CREATE query with an IF NOT EXISTS clause" in {
    BasicTable.create.ifNotExists.queryString shouldEqual "CREATE TABLE IF NOT EXISTS `BasicTable`;"
  }

  it should "serialise a CREATE query with a TEMPORARY clause" in {
    BasicTable.create.temporary.queryString shouldEqual "CREATE TEMPORARY TABLE `BasicTable`;"
  }

  it should "serialise a CREATE create query with a TEMPORARY clause" in {
    BasicTable.create.temporary.queryString shouldEqual "CREATE TEMPORARY TABLE `BasicTable`;"
  }

  ignore should "serialise a CREATE create query with a TEMPORARY clause and an IF NOT EXISTS clause" in {
    BasicTable.create.temporary.ifNotExists.queryString shouldEqual "CREATE TEMPORARY TABLE IF NOT EXISTS `BasicTable`;"
  }

  it should "serialise a complete table definition when an engine is specified" in {
    BasicTable.create.engine(InnoDB).queryString shouldEqual "CREATE TABLE `BasicTable` (`name` TEXT, `count` LONG) ENGINE InnoDB;"
  }

  it should "serialise a complete table definition with an IF NOT EXSITS clause when an engine is specified" in {
    BasicTable.create.ifNotExists.engine(InnoDB)
      .queryString shouldEqual "CREATE TABLE IF NOT EXISTS `BasicTable` (`name` TEXT, `count` LONG) ENGINE InnoDB;"
  }


}
