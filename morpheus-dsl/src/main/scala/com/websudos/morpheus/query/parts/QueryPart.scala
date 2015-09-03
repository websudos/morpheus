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
package com.websudos.morpheus.query.parts

import com.websudos.diesel.engine.query.multiparts.{QueryPart, MergedQueryList}
import com.websudos.morpheus.builder.{DefaultQueryBuilder, SQLBuiltQuery}

class SQLMergeList(override val list: List[SQLBuiltQuery]) extends MergedQueryList[SQLBuiltQuery](list) {

  override def apply(list: List[SQLBuiltQuery]): MergedQueryList[SQLBuiltQuery] = new SQLMergeList(list)

  override def apply(str: String): SQLBuiltQuery = SQLBuiltQuery(str)
}

abstract class SQLQueryPart[Part <: SQLQueryPart[Part]](override val list: List[SQLBuiltQuery]) extends QueryPart[Part, SQLBuiltQuery](list) {
  override def mergeList(list: List[SQLBuiltQuery]): MergedQueryList[SQLBuiltQuery] = new SQLMergeList(list)
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
