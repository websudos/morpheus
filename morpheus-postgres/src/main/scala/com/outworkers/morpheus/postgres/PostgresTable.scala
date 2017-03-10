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
package com.outworkers.morpheus.postgres

import com.outworkers.morpheus.Row
import com.outworkers.morpheus.builder.{AbstractQueryBuilder, AbstractSQLSyntax}
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine.query.{RootCreateQuery, RootUpdateQuery, _}

trait PostgresRow extends Row

abstract class PostgresTable[Owner <: PostgresTable[Owner, Record], Record] extends BaseTable[Owner, Record, PostgresRow] {

  val queryBuilder: AbstractQueryBuilder = PostgresQueryBuilder

  override protected[this] def syntax: AbstractSQLSyntax = PostgresSyntax

  override def update: RootUpdateQuery[Owner, Record, PostgresRow] = ???

  override def insert: RootInsertQuery[Owner, Record, PostgresRow] = ???

  override def delete: RootDeleteQuery[Owner, Record, PostgresRow] = ???

  override def create: RootCreateQuery[Owner, Record, PostgresRow] = ???

}
