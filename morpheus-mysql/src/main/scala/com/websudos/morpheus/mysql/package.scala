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

package com.websudos.morpheus

import com.websudos.morpheus.column.{AbstractColumn, DefaultForeignKeyConstraints}
import com.websudos.morpheus.dsl.DefaultImportsDefinition
import com.websudos.morpheus.mysql.query.{MySQLRootSelectQuery, MySQLSelectQuery}
import com.websudos.morpheus.operators.MySQLOperatorSet
import com.websudos.morpheus.query.{AssignUnchainned, Unchainned, Ungroupped, Unlimited, Unordered, Unterminated}

import scala.util.Try


package object mysql extends DefaultImportsDefinition
  with MySQLImplicits
  with MySQLPrimitives
  with MySQLOperatorSet
  with MySQLColumns
  with MySQLKeys
  with MySQLPrimitiveColumns
  with DefaultForeignKeyConstraints {

  override implicit def columnToQueryColumn[T : SQLPrimitive](col: AbstractColumn[T]): MySQLQueryColumn[T] = new MySQLQueryColumn[T](col)

  implicit def rootSelectQueryToQuery[T <: Table[T, _], R](root: MySQLRootSelectQuery[T, R]): MySQLSelectQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLSelectQuery(
      root.table,
      root.st.*,
      root.rowFunc
    )
  }

  type Row = com.websudos.morpheus.mysql.MySQLRow
  type Result = com.websudos.morpheus.mysql.MySQLResult

  type Table[Owner <: BaseTable[Owner, Record, MySQLRow], Record] = com.websudos.morpheus.mysql.MySQLTable[Owner, Record]

  def enumToQueryConditionPrimitive[T <: Enumeration](enum: T)(implicit ev: SQLPrimitive[String]): SQLPrimitive[T#Value] = {
    new SQLPrimitive[T#Value] {

      override def sqlType: String = ev.sqlType

      override def fromRow(row: com.websudos.morpheus.Row, name: String): Try[T#Value] = {
        Try { enum.withName(row.string(name)) }
      }

      override def toSQL(value: T#Value): String = ev.toSQL(value.toString)
    }
  }
}
