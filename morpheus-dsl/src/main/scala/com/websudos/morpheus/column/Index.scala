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

package com.websudos.morpheus.column

import com.websudos.morpheus.Row
import com.websudos.morpheus.builder.{SQLBuiltQuery, DefaultSQLSyntax}
import com.websudos.morpheus.dsl.BaseTable
import shapeless.<:!<

private[morpheus] trait IndexColumn {

  type NonIndexColumn[Owner <: BaseTable[Owner, _, _]] = Column[Owner, _, _, _]

  def apply(r: Row): String = throw new Exception(s"Index column is not a value column. This apply method cannot extract anything from it.")
}

/**
 * This implements an SQL index column. With this the user can define indexes on a table.
 * An Index is also an implementation of an Indexed column, meaning it holds no concrete value to be extracted.
 *
 * Using the "not a descendant of" bound, the constructor is also ensuring an Index cannot be built out of other indexes. That would be invalid with respect
 * to the SQL syntax rules.
 *
 * @param columns The columns that form the
 * @tparam T The table owning the Record.
 * @tparam R The record of the table.
 */
abstract class AbstractIndex[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](columns: IndexColumn#NonIndexColumn[_]*)(implicit ev:
  IndexColumn#NonIndexColumn[_] <:!< IndexColumn) extends AbstractColumn[String] with IndexColumn {

  def qb: SQLBuiltQuery = {
    SQLBuiltQuery(DefaultSQLSyntax.index)
      .forcePad.append(DefaultSQLSyntax.`(`)
      .append(columns.map(_.name).mkString(", "))
      .append(DefaultSQLSyntax.`)`)
  }

  override def sqlType: String = "string"

  override def toQueryString(v: String): String = v

  override def table: BaseTable[_, _, _] = columns.head.table
}
