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

package com.websudos.morpheus.mysql

import com.websudos.morpheus.dsl.SelectTable
import com.websudos.morpheus.mysql.query._

abstract class MySQLTable[Owner <: BaseTable[Owner, Record, MySQLRow], Record]
  extends BaseTable[Owner, Record, MySQLRow]
  with SelectTable[Owner, Record, MySQLRow, MySQLRootSelectQuery, MySQLSelectSyntaxBlock] {

  val queryBuilder = MySQLQueryBuilder

  val syntax = MySQLSyntax

  protected[this] def createRootSelect[A <: BaseTable[A, _, MySQLRow], B](table: A, block: MySQLSelectSyntaxBlock, rowFunc: MySQLRow => B): MySQLRootSelectQuery[A, B] = {
    new MySQLRootSelectQuery[A, B](table, block, rowFunc)
  }

  protected[this] def createSelectSyntaxBlock(query: String, tableName: String, cols: List[String] = List("*")): MySQLSelectSyntaxBlock = {
    new MySQLSelectSyntaxBlock(query, tableName, cols)
  }

  def update: MySQLRootUpdateQuery[Owner, Record] = new MySQLRootUpdateQuery(
    this.asInstanceOf[Owner],
    MySQLUpdateSyntaxBlock(syntax.update, tableName),
    fromRow
  )

  def delete: MySQLRootDeleteQuery[Owner, Record] = new MySQLRootDeleteQuery(
    this.asInstanceOf[Owner],
    MySQLDeleteSyntaxBlock(syntax.delete, tableName),
    fromRow
  )

  def insert: MySQLRootInsertQuery[Owner, Record] = new MySQLRootInsertQuery(
    this.asInstanceOf[Owner],
    new MySQLInsertSyntaxBlock(syntax.insert, tableName),
    fromRow
  )

  def create: MySQLRootCreateQuery[Owner, Record] = new MySQLRootCreateQuery(
    this.asInstanceOf[Owner],
    new MySQLCreateSyntaxBLock(syntax.create, tableName),
    fromRow
  )

}
