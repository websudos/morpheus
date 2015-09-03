/*
 * Copyright 2013-2015 Websudos, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Explicit consent must be obtained from the copyright owner, Websudos Limited before any redistribution is made.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.websudos.morpheus.query

import com.websudos.morpheus.builder.{SQLBuiltQuery, DefaultQueryBuilder, DefaultSQLSyntax}
import com.websudos.morpheus.{Row, SQLPrimitive}
import com.websudos.morpheus.column.AbstractColumn

private[morpheus] abstract class BaseQueryCondition(val clause: SQLBuiltQuery)


case class QueryAssignment(clause: SQLBuiltQuery)

abstract class SelectOperatorClause[T : SQLPrimitive](val qb: SQLBuiltQuery) {
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


case class BetweenClause[T: SQLPrimitive](qb: SQLBuiltQuery) {

  def and(value: T): QueryCondition = {
    QueryCondition(
      qb.forcePad
        .append(DefaultSQLSyntax.and)
        .forcePad.append(implicitly[SQLPrimitive[T]].toSQL(value))
    )
  }
}

/**
 * This encloses the full list of available comparison operators.
 * @param col The column to cast to an IndexedColumn.
 * @tparam T The type of the value the column holds.
 */
private[morpheus] abstract class AbstractQueryColumn[T: SQLPrimitive](col: AbstractColumn[T]) {

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
    val primitive = implicitly[SQLPrimitive[T]]
    QueryCondition(col.table.queryBuilder.in(col.name, values.map(primitive.toSQL)))
  }

  def in(values: T*): QueryCondition = {
    val primitive = implicitly[SQLPrimitive[T]]
    QueryCondition(col.table.queryBuilder.in(col.name, values.map(primitive.toSQL)))
  }

  def notIn(values: List[T]) : QueryCondition = {
    val primitive = implicitly[SQLPrimitive[T]]
    QueryCondition(col.table.queryBuilder.notIn(col.name, values.map(primitive.toSQL)))
  }

  def notIn(values: T*) : QueryCondition = {
    val primitive = implicitly[SQLPrimitive[T]]
    QueryCondition(col.table.queryBuilder.notIn(col.name, values.map(primitive.toSQL)))
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

class SQLQueryColumn[T: SQLPrimitive](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)


case class QueryOrder(clause: SQLBuiltQuery)

abstract class AbstractOrderingColumn[T: SQLPrimitive](col: AbstractColumn[T]) {
  def asc: QueryOrder = QueryOrder(col.table.queryBuilder.asc(col.name))
  def desc: QueryOrder = QueryOrder(col.table.queryBuilder.desc(col.name))
}
