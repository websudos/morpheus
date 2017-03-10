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
package com.outworkers.morpheus.sql

import com.outworkers.morpheus.builder.{DefaultQueryBuilder, DefaultSQLSyntax}
import com.outworkers.morpheus.dsl.SelectTable
import com.outworkers.morpheus.engine.query._
import com.outworkers.morpheus.{Row => MorpheusRow}

trait DefaultRow extends MorpheusRow

private[morpheus] class MySQLRootSelectQuery[
  T <: BaseTable[T, _, DefaultRow],
  R
](table: T, st: AbstractSelectSyntaxBlock, rowFunc: DefaultRow => R)
  extends AbstractRootSelectQuery[T, R, DefaultRow](table, st, rowFunc) {
}

abstract class SQLTable[
  Owner <: BaseTable[Owner, Record, DefaultRow],
  Record
] extends BaseTable[Owner, Record, DefaultRow] with SelectTable[Owner, Record, DefaultRow, DefaultRootSelectQuery, AbstractSelectSyntaxBlock] {

  val queryBuilder = DefaultQueryBuilder

  val syntax = DefaultSQLSyntax

  protected[this] def createRootSelect[
    A <: BaseTable[A, _, DefaultRow],
    B
  ](table: A, block: AbstractSelectSyntaxBlock, rowFunc: DefaultRow => B): DefaultRootSelectQuery[A, B] = {
    new DefaultRootSelectQuery[A, B](table, block, rowFunc)
  }

  protected[this] def selectBlock(
    query: String,
    tableName: String,
    cols: List[String] = List("*")
  ): AbstractSelectSyntaxBlock = {
    new AbstractSelectSyntaxBlock(query, tableName, cols)
  }

  def update: DefaultRootUpdateQuery[Owner, Record] = new DefaultRootUpdateQuery(
    this.asInstanceOf[Owner],
    new RootUpdateSyntaxBlock(syntax.update, tableName),
    fromRow
  )

  def delete: DefaultRootDeleteQuery[Owner, Record] = new DefaultRootDeleteQuery(
    this.asInstanceOf[Owner],
    new RootDeleteSyntaxBlock(syntax.delete, tableName),
    fromRow
  )

  def insert: DefaultRootInsertQuery[Owner, Record] = new DefaultRootInsertQuery(
    this.asInstanceOf[Owner],
    new RootInsertSyntaxBlock(syntax.insert, tableName),
    fromRow
  )

  def create: DefaultRootCreateQuery[Owner, Record] = new DefaultRootCreateQuery(
    this.asInstanceOf[Owner],
    new RootCreateSyntaxBlock(syntax.create, tableName),
    fromRow
  )

}
