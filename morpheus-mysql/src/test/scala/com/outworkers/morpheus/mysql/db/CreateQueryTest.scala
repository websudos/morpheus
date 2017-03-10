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
package com.outworkers.morpheus.mysql.db

import com.outworkers.morpheus.mysql.tables.BasicTable
import org.scalatest.FlatSpec
import com.outworkers.morpheus.mysql.dsl._

class CreateQueryTest extends FlatSpec with BaseSuite {

  it should "create a new table in the MySQL database" in {
    whenReady(BasicTable.create.temporary.engine(InnoDB).future) {
      res =>
    }
  }

  it should "create a new table in the database if the table doesn't exist" in {
    whenReady(BasicTable.create.ifNotExists.engine(InnoDB).future) { _ => }
  }

}
