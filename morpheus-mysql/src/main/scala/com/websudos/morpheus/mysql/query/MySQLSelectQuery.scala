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

package com.websudos.morpheus.mysql.query

import com.twitter.util.Future
import com.websudos.morpheus.Client
import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.mysql._
import com.websudos.morpheus.query._

import scala.annotation.implicitNotFound
import scala.concurrent.{Future => ScalaFuture}


private[morpheus] class MySQLSelectSyntaxBlock(
  query: String, tableName: String,
  columns: List[String] = List("*")) extends AbstractSelectSyntaxBlock(query, tableName, columns) {
  override val syntax = MySQLSyntax

  private[this] def selector(quantifier: String, columns: List[String], table: String): SQLBuiltQuery = {
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


private[morpheus] class MySQLRootSelectQuery[T <: BaseTable[T, _, MySQLRow], R](table: T, st: MySQLSelectSyntaxBlock, rowFunc: MySQLRow => R)
  extends AbstractRootSelectQuery[T, R, MySQLRow](table, st, rowFunc) {

  type BaseSelectQuery = MySQLSelectQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]

  def distinctRow: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.distinctRow, rowFunc)
  }

  def highPriority: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.highPriority, rowFunc)
  }

  def straightJoin: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.straightJoin, rowFunc)
  }

  def sqlSmallResult: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlSmallResult, rowFunc)
  }

  def sqlBigResult: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlBigResult, rowFunc)
  }

  def sqlBufferResult: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlBufferResult, rowFunc)
  }

  def sqlCache: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlCache, rowFunc)
  }

  def sqlNoCache: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlNoCache, rowFunc)
  }

  def sqlCalcFoundRows: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlCalcFoundRows, rowFunc)
  }
}


class MySQLSelectQuery[T <: BaseTable[T, _, MySQLRow],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](table: T, query: SQLBuiltQuery, rowFunc: MySQLRow => R)
  extends SelectQuery[T, R, MySQLRow, Group, Order, Limit, Chain, AssignChain, Status](table, query, rowFunc)
  with SQLResultsQuery[T, R, MySQLRow, MySQLResult, Limit] {

  @implicitNotFound("You cannot use two where clauses on a single query")
  override def where(condition: T => QueryCondition)(implicit ev: Chain =:= Unchainned): MySQLSelectQuery[T, R, Group, Order, Limit, Chainned,
    AssignChain, Status] = {
    new MySQLSelectQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  override def where(condition: QueryCondition)(implicit ev: Chain =:= Unchainned): MySQLSelectQuery[T, R, Group, Order, Limit, Chainned,
    AssignChain, Status] = {
    new MySQLSelectQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  override def and(condition: T => QueryCondition)(implicit ev: Chain =:= Chainned): MySQLSelectQuery[T, R, Group, Order, Limit, Chain,
    AssignChainned, Status]  = {
    new MySQLSelectQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  override def and(condition: QueryCondition)(implicit ev: Chain =:= Chainned): MySQLSelectQuery[T, R, Group, Order, Limit, Chain, AssignChainned,
    Status] = {
    new MySQLSelectQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  /**
   * Returns the first row from the select ignoring everything else.
   * @param client The MySQL client in use.
   * @return
   */
  override def one()(implicit client: Client[MySQLRow, MySQLResult], ev: Limit =:= Unlimited): ScalaFuture[Option[R]] = {
    twitterToScala(get)
  }

  /**
   * Get the result of an operation as a Twitter Future.
   * @param client The MySQL client in use.
   * @return A Twitter future wrapping the result.
   */
  override def get()(implicit client: Client[MySQLRow, MySQLResult], ev: Limit =:= Unlimited): Future[Option[R]] = {
    client.select(limit(1).queryString)(fromRow) map (_.headOption)
  }
}
