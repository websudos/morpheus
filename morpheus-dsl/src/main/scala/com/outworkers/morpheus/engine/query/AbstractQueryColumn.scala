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

import com.outworkers.morpheus.DataType
import com.outworkers.morpheus.builder.{DefaultQueryBuilder, DefaultSQLSyntax, SQLBuiltQuery}
import com.outworkers.morpheus.Row
import com.outworkers.morpheus.column.AbstractColumn

private[morpheus] abstract class BaseQueryCondition(val clause: SQLBuiltQuery)


case class QueryAssignment(clause: SQLBuiltQuery)

abstract class SelectOperatorClause[T : DataType](val qb: SQLBuiltQuery) {
  def fromRow(row: Row): T
}

/**
 * This is a wrapper clause for primary conditions.
 * They wrap the Clause used in a "WHERE" or "AND" query.
 *
 * Only indexed columns can produce a QueryCondition via "WHERE" and "AND" operators.
 * @param clause The clause to use.
 */
case class QueryCondition(override val clause: SQLBuiltQuery, count: Int = 0) extends BaseQueryCondition(clause) {

  /**
   * This implementation is used to handle enclosing parentheses for an unknown number of OR operator usages.
   * Since an unlimited number of OR operators and conditions can be chained to form a single WHERE or AND clause,
   * we need a way to delimit the full clause by enclosing parentheses without knowing how many OR clauses there are or without knowing what the internals
   * of a clause look like. Clauses like the IN clause have their own set of parentheses.
   * An example: {@code SELECT* FROM something WHERE (a = 5 OR a in (5, 10, 15)) }.
   *
   * Using the count parameter we can count the number of combinations in a manner invisible to the user. If the count is 0,
   * append the left '(' and the right ')' and for everyone thereafter, remove the ')', add the new clause, add a ')',
   * effectively always moving the right ')' to the end of the full WHERE or AND clause.
   *
   * @param condition The QueryCondition to OR with.
   * @return A new QueryCondition, where the underlying query has been OR-ed.
   */
  def or(condition: QueryCondition): QueryCondition = {
    if (count == 0) {
      QueryCondition(DefaultQueryBuilder.or(
        clause.prepend(DefaultSQLSyntax.`(`),
        condition.clause.append(DefaultSQLSyntax.`)`)),
        count + 1
      )
    } else {
      QueryCondition(DefaultQueryBuilder.or(
        SQLBuiltQuery(clause.queryString.dropRight(1)),
        condition.clause.append(DefaultSQLSyntax.`)`)),
        count + 1
      )
    }
  }
}


case class BetweenClause[T: DataType](qb: SQLBuiltQuery) {

  def and(value: T): QueryCondition = {
    QueryCondition(
      qb.forcePad
        .append(DefaultSQLSyntax.and)
        .forcePad.append(implicitly[DataType[T]].serialize(value))
    )
  }
}

/**
 * This encloses the full list of available comparison operators.
 * @param col The column to cast to an IndexedColumn.
 * @tparam T The type of the value the column holds.
 */
private[morpheus] abstract class AbstractQueryColumn[T: DataType](col: AbstractColumn[T]) {

  /**
   * The equals operator. Will return a match if the value equals the database value.
   * @param value The value to search for in the database.
   * @return A QueryCondition, wrapping a QueryBuilder clause.
   */
  def eqs(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.eqs(col.name, col.toQueryString(value)))
  }

  def lt(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.lt(col.name, col.toQueryString(value)))
  }

  def <(value: T): QueryCondition = lt(value)

  def lte(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.lte(col.name, col.toQueryString(value)))
  }

  def <=(value: T): QueryCondition = lte(value)

  def gt(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.gt(col.name, col.toQueryString(value)))
  }

  def >(value: T): QueryCondition = gt(value)

  def gte(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.gte(col.name, col.toQueryString(value)))
  }

  def >=(value: T): QueryCondition = gte(value)

  def !=(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.!=(col.name, col.toQueryString(value)))
  }

  def <>(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.!=(col.name, col.toQueryString(value)))
  }

  def like(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.like(col.name, col.toQueryString(value)))
  }

  def notLike(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.notLike(col.name, col.toQueryString(value)))
  }

  def in(values: List[T]) : QueryCondition = {
    val primitive = implicitly[DataType[T]]
    QueryCondition(col.table.queryBuilder.in(col.name, values.map(primitive.serialize)))
  }

  def in(values: T*): QueryCondition = {
    val primitive = implicitly[DataType[T]]
    QueryCondition(col.table.queryBuilder.in(col.name, values.map(primitive.serialize)))
  }

  def notIn(values: List[T]) : QueryCondition = {
    val primitive = implicitly[DataType[T]]
    QueryCondition(col.table.queryBuilder.notIn(col.name, values.map(primitive.serialize)))
  }

  def notIn(values: T*) : QueryCondition = {
    val primitive = implicitly[DataType[T]]
    QueryCondition(col.table.queryBuilder.notIn(col.name, values.map(primitive.serialize)))
  }

  def isNull: QueryCondition = {
    QueryCondition(col.table.queryBuilder.isNull(SQLBuiltQuery(col.name)))
  }

  def isNotNull: QueryCondition = {
    QueryCondition(col.table.queryBuilder.isNotNull(SQLBuiltQuery(col.name)))
  }

  def between(value: T): BetweenClause[T] = {
    BetweenClause(
      col.table.queryBuilder.between(col.name, col.toQueryString(value))
    )
  }

  def notBetween(value: T): BetweenClause[T] = {
    BetweenClause(
      col.table.queryBuilder.notBetween(col.name, col.toQueryString(value))
    )
  }
}

class SQLQueryColumn[T: DataType](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)


case class QueryOrder(clause: SQLBuiltQuery)

abstract class AbstractOrderingColumn[T: DataType](col: AbstractColumn[T]) {
  def asc: QueryOrder = QueryOrder(col.table.queryBuilder.asc(col.name))
  def desc: QueryOrder = QueryOrder(col.table.queryBuilder.desc(col.name))
}
