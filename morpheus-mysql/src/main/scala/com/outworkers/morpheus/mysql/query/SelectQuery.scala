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

import com.outworkers.morpheus.mysql.{Result, Row, Syntax}
import com.outworkers.morpheus.engine.query._
import com.twitter.util.Future
import com.outworkers.morpheus.Client
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine
import shapeless.{HList, HNil}

import scala.annotation.implicitNotFound
import scala.concurrent.{Future => ScalaFuture}

private[morpheus] class SelectSyntaxBlock(
  query: String,
  tableName: String,
  columns: List[String] = "*" :: Nil
) extends engine.query.AbstractSelectSyntaxBlock(query, tableName, columns) {
  override val syntax = Syntax

  def selector(quantifier: String, columns: List[String], table: String): SQLBuiltQuery = {
    qb.pad.append(quantifier)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def distinctRow: SQLBuiltQuery = {
    selector(syntax.SelectOptions.distinctRow, columns, tableName)
  }

  def highPriority: SQLBuiltQuery = {
    selector(syntax.Priorities.highPriority, columns, tableName)
  }

  def straightJoin: SQLBuiltQuery = {
    selector(syntax.SelectOptions.straightJoin, columns, tableName)
  }

  def sqlSmallResult: SQLBuiltQuery = {
    selector(syntax.SelectOptions.sqlSmallResult, columns, tableName)
  }

  def sqlBigResult: SQLBuiltQuery = {
    selector(syntax.SelectOptions.sqlBigResult, columns, tableName)
  }

  def sqlBufferResult: SQLBuiltQuery = {
    selector(syntax.SelectOptions.sqlBufferResult, columns, tableName)
  }

  def sqlCache: SQLBuiltQuery = {
    selector(syntax.SelectOptions.sqlCache, columns, tableName)
  }

  def sqlNoCache: SQLBuiltQuery = {
    selector(syntax.SelectOptions.sqlNoCache, columns, tableName)
  }

  def sqlCalcFoundRows: SQLBuiltQuery = {
    selector(syntax.SelectOptions.sqlCalcFoundRows, columns, tableName)
  }
}


private[morpheus] class RootSelectQuery[
  T <: BaseTable[T, _, Row],
  R
](
  table: T,
  st: SelectSyntaxBlock,
  rowFunc: Row => R
) extends engine.query.AbstractRootSelectQuery[T, R, Row](table, st, rowFunc) {

  type BaseSelectQuery = SelectQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil]

  def distinctRow: BaseSelectQuery = {
    new SelectQuery(table, st.distinctRow, rowFunc)
  }

  def highPriority: BaseSelectQuery = {
    new SelectQuery(table, st.highPriority, rowFunc)
  }

  def straightJoin: BaseSelectQuery = {
    new SelectQuery(table, st.straightJoin, rowFunc)
  }

  def sqlSmallResult: BaseSelectQuery = {
    new SelectQuery(table, st.sqlSmallResult, rowFunc)
  }

  def sqlBigResult: BaseSelectQuery = {
    new SelectQuery(table, st.sqlBigResult, rowFunc)
  }

  def sqlBufferResult: BaseSelectQuery = {
    new SelectQuery(table, st.sqlBufferResult, rowFunc)
  }

  def sqlCache: BaseSelectQuery = {
    new SelectQuery(table, st.sqlCache, rowFunc)
  }

  def sqlNoCache: BaseSelectQuery = {
    new SelectQuery(table, st.sqlNoCache, rowFunc)
  }

  def sqlCalcFoundRows: BaseSelectQuery = {
    new SelectQuery(table, st.sqlCalcFoundRows, rowFunc)
  }
}


class SelectQuery[T <: BaseTable[T, _, Row],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](table: T, query: SQLBuiltQuery, rowFunc: Row => R)
  extends engine.query.SelectQuery[T, R, Row, Group, Order, Limit, Chain, AssignChain, Status](table, query, rowFunc)
  with SQLResultsQuery[T, R, Row, Result, Limit] {

  @implicitNotFound("You cannot use two where clauses on a single query")
  override def where(condition: T => QueryCondition)(
    implicit ev: Chain =:= Unchainned
  ): SelectQuery[T, R, Group, Order, Limit, Chainned, AssignChain, Status] = {
    new SelectQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  override def where(condition: QueryCondition)(
    implicit ev: Chain =:= Unchainned
  ): SelectQuery[T, R, Group, Order, Limit, Chainned, AssignChain, Status] = {
    new SelectQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  override def and(condition: T => QueryCondition)(
    implicit ev: Chain =:= Chainned
  ): SelectQuery[T, R, Group, Order, Limit, Chain, AssignChainned, Status] = {
    new SelectQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  override def and(condition: QueryCondition)(
    implicit ev: Chain =:= Chainned
  ): SelectQuery[T, R, Group, Order, Limit, Chain, AssignChainned, Status] = {
    new SelectQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  /**
    * Returns the first row from the select ignoring everything else.
    *
    * @param client The MySQL client in use.
    * @return
    */
  override def one()(implicit client: Client[Row, Result], ev: Limit =:= Unlimited): ScalaFuture[Option[R]] = {
    twitterToScala(get)
  }

  /**
    * Get the result of an operation as a Twitter Future.
    *
    * @param client The MySQL client in use.
    * @return A Twitter future wrapping the result.
    */
  override def get()(implicit client: Client[Row, Result], ev: Limit =:= Unlimited): Future[Option[R]] = {
    client.select(limit(1).queryString)(fromRow) map (_.headOption)
  }
}
