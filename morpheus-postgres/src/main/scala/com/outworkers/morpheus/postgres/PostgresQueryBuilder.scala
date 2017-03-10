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

import com.outworkers.morpheus.builder.{AbstractQueryBuilder, AbstractSQLSyntax, SQLOperatorSet}

sealed class PostgresOperatorSet extends SQLOperatorSet {

}

object PostgresOperatorSet extends PostgresOperatorSet


sealed class PostgresQueryBuilder extends AbstractQueryBuilder {
  val operators: SQLOperatorSet = PostgresOperatorSet

  val syntax: AbstractSQLSyntax = PostgresSyntax
}

object PostgresQueryBuilder extends PostgresQueryBuilder
