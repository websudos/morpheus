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

import com.outworkers.morpheus.Row
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.column.SelectColumn
import com.outworkers.morpheus.dsl.BaseTable
import shapeless.HList

import scala.annotation.implicitNotFound

/**
 * This bit of magic allows all extending sub-classes to implement the "where" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments, all queries such as UPDATE,
 * DELETE, ALTER and so on can use the same root implementation of clauses and therefore avoid the violation of DRY.
 *
 * The reason why the "clause" and "andClause" methods below are protected is so that extending classes can decide when and how to expose "where" and "and"
 * SQL methods to the DSL user. Used mainly to make queries like "select.where(_.a = b).where(_.c = d)" impossible,
 * or in other words make illegal programming states unrepresentable. There is an awesome book about how to do this in Scala,
 * I will link to it as soon as the book is published.
 *
 * @param table The table owning the record.
 * @param init The root SQL query to start building from.
 * @param rowFunc The function mapping a row to a record.
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
abstract class Query[T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row,
  Group <: GroupBind,
  Ord <: OrderBind,
  Lim <: LimitBind,
  Chain <: ChainBind,
  AC <: AssignBind,
  PS <: HList
](
  val table: T,
  val init: SQLBuiltQuery,
  val rowFunc: TableRow => R
) extends SQLQuery[T, R, TableRow] {

  protected[this] type QueryType[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ] <: Query[T, R, TableRow, G, O, L, S, C, P]

  protected[this] def create[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ](t: T, q: SQLBuiltQuery, r: TableRow => R): QueryType[G, O, L, S, C, P]

  def fromRow(row: TableRow): R = rowFunc(row)

  @implicitNotFound("You cannot set two limits on the same query")
  final def limit(value: Int)(implicit ev: Lim =:= Unlimited): QueryType[Group, Ord, Limited, Chain, AC, PS] = {
    create(table, table.queryBuilder.limit(query, value.toString), rowFunc)
  }

  @implicitNotFound("You cannot ORDER a query more than once")
  final def orderBy(conditions: (T => QueryOrder)*)(
    implicit ev: Ord =:= Unordered
  ): QueryType[Group, Ordered, Lim, Chain, AC, PS] = {
    val applied = conditions map {
      fn => fn(table).clause
    }

    create(table, table.queryBuilder.orderBy(query, applied), rowFunc)
  }

  @implicitNotFound("You cannot GROUP a query more than once or GROUP after you ORDER a query")
  final def groupBy(columns: (T => SelectColumn[_])*)(
    implicit ev1: Group =:= Ungroupped,
    ev2: Ord =:= Unordered
  ): QueryType[Groupped, Ord, Lim, Chain, AC, PS] = {
    create(table, table.queryBuilder.groupBy(query, columns map { _(table).queryString }), rowFunc)
  }

}
