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
package com.outworkers
package morpheus
package mysql

import com.outworkers.morpheus.dsl.{BaseTable, SelectTable}
import com.outworkers.morpheus.mysql.query._

abstract class Table[Owner <: BaseTable[Owner, Record, mysql.Row], Record]
  extends BaseTable[Owner, Record, Row] with SelectTable[Owner, Record, Row, RootSelectQuery, SelectSyntaxBlock] {

  val queryBuilder = QueryBuilder

  val syntax = Syntax

  protected[this] def createRootSelect[A <: BaseTable[A, _, Row], B](
    table: A,
    block: SelectSyntaxBlock,
    rowFunc: Row => B
  ): RootSelectQuery[A, B] = {
    new RootSelectQuery[A, B](table, block, rowFunc)
  }

  protected[this] def selectBlock(query: String, tableName: String, cols: List[String] = List("*")): SelectSyntaxBlock = {
    new SelectSyntaxBlock(query, tableName, cols)
  }

  def update: RootUpdateQuery[Owner, Record] = new RootUpdateQuery(
    this.asInstanceOf[Owner],
    UpdateSyntaxBlock(syntax.update, tableName),
    fromRow
  )

  def delete: RootDeleteQuery[Owner, Record] = new RootDeleteQuery(
    this.asInstanceOf[Owner],
    DeleteSyntaxBlock(syntax.delete, tableName),
    fromRow
  )

  def insert: RootInsertQuery[Owner, Record] = new RootInsertQuery(
    this.asInstanceOf[Owner],
    new InsertSyntaxBlock(syntax.insert, tableName),
    fromRow
  )

  def create: RootCreateQuery[Owner, Record] = new RootCreateQuery(
    this.asInstanceOf[Owner],
    new CreateSyntaxBlock(syntax.create, tableName),
    fromRow
  )

}
