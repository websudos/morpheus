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
package com.websudos.morpheus.sql


import com.websudos.morpheus.builder.{DefaultQueryBuilder, DefaultSQLSyntax}
import com.websudos.morpheus.dsl.SelectTable
import com.websudos.morpheus.query._
import com.websudos.morpheus.{ Row => MorpheusRow }

trait DefaultRow extends MorpheusRow {}

private[morpheus] class MySQLRootSelectQuery[T <: BaseTable[T, _, DefaultRow], R](table: T, st: AbstractSelectSyntaxBlock, rowFunc: DefaultRow => R)
  extends AbstractRootSelectQuery[T, R, DefaultRow](table, st, rowFunc) {
}

abstract class SQLTable[Owner <: BaseTable[Owner, Record, DefaultRow], Record] extends BaseTable[Owner, Record, DefaultRow] with SelectTable[Owner,
  Record, DefaultRow, DefaultRootSelectQuery, AbstractSelectSyntaxBlock] {

  val queryBuilder = DefaultQueryBuilder

  val syntax = DefaultSQLSyntax

  protected[this] def createRootSelect[A <: BaseTable[A, _, DefaultRow], B](table: A, block: AbstractSelectSyntaxBlock, rowFunc: DefaultRow => B):
  DefaultRootSelectQuery[A, B] = {
    new DefaultRootSelectQuery[A, B](table, block, rowFunc)
  }

  protected[this] def createSelectSyntaxBlock(query: String, tableName: String, cols: List[String] = List("*")): AbstractSelectSyntaxBlock = {
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
