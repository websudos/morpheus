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
import com.outworkers.morpheus.engine.query._
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine
import shapeless.{HList, HNil}

import scala.annotation.implicitNotFound

case class UpdateSyntaxBlock(query: String, tableName: String) extends engine.query.RootUpdateSyntaxBlock(query, tableName) {
  override val syntax = Syntax

  def lowPriority: SQLBuiltQuery = {
    qb.pad.append(syntax.Priorities.lowPriority)
      .pad.append(tableName)
  }

  def ignore: SQLBuiltQuery = {
    qb.pad.append(syntax.ignore)
      .pad.append(tableName)
  }
}


class RootUpdateQuery[T <: BaseTable[T, _, Row], R](table: T, st: UpdateSyntaxBlock, rowFunc: Row => R)
  extends engine.query.RootUpdateQuery[T, R, Row](table, st, rowFunc) {

  def lowPriority: UpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new UpdateQuery(table, st.lowPriority, rowFunc)
  }

  def ignore: UpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new UpdateQuery(table, st.ignore, rowFunc)
  }
}


class UpdateQuery[T <: BaseTable[T, _, Row],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](
  table: T,
  query: SQLBuiltQuery,
  rowFunc: Row => R
) extends engine.query.UpdateQuery[T, R, Row, Group, Order, Limit, Chain, AssignChain, Status](table: T, query, rowFunc) {

  override def where(condition: T => QueryCondition)(
    implicit ev: Chain =:= Unchainned
  ): UpdateQuery[T, R, Group, Order, Limit, Chainned, AssignChain, Status] = {
    new UpdateQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  override def where(condition: QueryCondition)(implicit ev: Chain =:= Unchainned): UpdateQuery[T, R, Group, Order, Limit, Chainned,
    AssignChain, Status] = {
    new UpdateQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  override def and(condition: T => QueryCondition)(implicit ev: Chain =:= Chainned): UpdateQuery[T, R, Group, Order, Limit, Chain,
    AssignChainned, Status]  = {
    new UpdateQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  override def and(condition: QueryCondition)(implicit ev: Chain =:= Chainned): UpdateQuery[T, R, Group, Order, Limit, Chain, AssignChainned,
    Status]  = {
    new UpdateQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  override def set(condition: T => QueryAssignment)(implicit ev: AssignChain =:= AssignUnchainned, ev1: Status =:= HNil): UpdateQuery[T, R,
    Group, Order, Limit, Chain, AssignChainned, Status] = {
    new UpdateQuery(
      table,
      table.queryBuilder.set(query, condition(table).clause),
      rowFunc
    )
  }

  @implicitNotFound("""You need to use the "set" method before using the "and"""")
  override def andSet(condition: T => QueryAssignment, signChange: Int = 0)(implicit ev: AssignChain =:= AssignChainned): UpdateQuery[T, R,
    Group, Order, Limit, Chain, AssignChainned, Status] = {
    new UpdateQuery(
      table,
      table.queryBuilder.andSet(query, condition(table).clause),
      rowFunc
    )
  }
}
