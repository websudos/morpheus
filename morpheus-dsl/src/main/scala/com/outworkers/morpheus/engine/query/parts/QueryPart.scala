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
package com.outworkers.morpheus.engine.query.parts

import com.outworkers.morpheus.builder.{DefaultQueryBuilder, SQLBuiltQuery}

abstract class SQLQueryPart[
  Part <: SQLQueryPart[Part]
](override val list: List[SQLBuiltQuery]) extends QueryPart[Part](list) {
  override def mergeList(list: List[SQLBuiltQuery]): MergeList = new MergeList(list)
}

sealed class WherePart(override val list: List[SQLBuiltQuery] = Nil) extends SQLQueryPart[WherePart](list) {
  override def qb: SQLBuiltQuery = DefaultQueryBuilder.clauses(list)

  override def instance(list: List[SQLBuiltQuery]): WherePart = new WherePart(list)
}

sealed class LimitedPart(override val list: List[SQLBuiltQuery] = Nil) extends SQLQueryPart[LimitedPart](list) {
  override def qb: SQLBuiltQuery = DefaultQueryBuilder.clauses(list)

  override def instance(l: List[SQLBuiltQuery]): LimitedPart = new LimitedPart(l)
}

sealed class OrderPart(override val list: List[SQLBuiltQuery] = Nil) extends SQLQueryPart[OrderPart](list) {
  override def qb: SQLBuiltQuery = DefaultQueryBuilder.clauses(list)

  override def instance(l: List[SQLBuiltQuery]): OrderPart = new OrderPart(l)
}

sealed class SetPart(override val list: List[SQLBuiltQuery] = Nil) extends SQLQueryPart[SetPart](list) {
  override def qb: SQLBuiltQuery = DefaultQueryBuilder.clauses(list)

  override def instance(l: List[SQLBuiltQuery]): SetPart = new SetPart(l)
}

sealed class ColumnsPart(override val list: List[SQLBuiltQuery] = Nil) extends SQLQueryPart[ColumnsPart](list) {
  override def qb: SQLBuiltQuery = DefaultQueryBuilder.columns(list)

  override def instance(l: List[SQLBuiltQuery]): ColumnsPart = new ColumnsPart(l)
}

sealed class ValuePart(override val list: List[SQLBuiltQuery] = Nil) extends SQLQueryPart[ValuePart](list) {
  override def qb: SQLBuiltQuery = DefaultQueryBuilder.values(list)

  override def instance(l: List[SQLBuiltQuery]): ValuePart = new ValuePart(l)
}

sealed class LightweightPart(override val list: List[SQLBuiltQuery] = Nil) extends SQLQueryPart[LightweightPart](list) {
  override def qb: SQLBuiltQuery = DefaultQueryBuilder.clauses(list)

  override def instance(l: List[SQLBuiltQuery]): LightweightPart = new LightweightPart(l)
}

private[morpheus] object Defaults {
  val EmptyWherePart = new WherePart()
  val EmptySetPart = new SetPart()
  val EmptyLimitPart = new LimitedPart()
  val EmptyOrderPart = new OrderPart()
  val EmptyValuePart = new ValuePart()
  val EmptyColumnsPart = new ColumnsPart()
  val EmptyLightweightPart = new LightweightPart()
}
