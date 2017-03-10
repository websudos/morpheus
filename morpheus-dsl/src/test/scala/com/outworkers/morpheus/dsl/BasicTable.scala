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

import com.outworkers.morpheus.sql.DefaultRow
import com.outworkers.morpheus.sql._

case class BasicRecord(name: String, count: Long)

class BasicTable extends Table[BasicTable, BasicRecord] {

  object name extends TextColumn(this)
  object count extends LongColumn(this)

  def fromRow(row: DefaultRow): BasicRecord = {
    BasicRecord(name(row), count(row))
  }

}

object BasicTable extends BasicTable
