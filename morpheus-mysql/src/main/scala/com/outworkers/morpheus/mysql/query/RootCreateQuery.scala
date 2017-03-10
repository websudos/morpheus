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

import com.outworkers.morpheus.mysql.{Row, Syntax}
import com.outworkers.morpheus.builder.AbstractSQLSyntax
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine


class CreateSyntaxBlock(query: String, tableName: String) extends engine.query.RootCreateSyntaxBlock(query, tableName) {
  override def syntax: AbstractSQLSyntax = Syntax
}

class RootCreateQuery[T <: BaseTable[T, _, Row], R](
  table: T,
  st: CreateSyntaxBlock,
  rowFunc: Row => R
) extends engine.query.RootCreateQuery[T, R, Row](table, st, rowFunc)
