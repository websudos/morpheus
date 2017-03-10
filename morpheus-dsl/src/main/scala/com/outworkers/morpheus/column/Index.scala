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
package com.outworkers.morpheus.column

import com.outworkers.morpheus.builder.{DefaultSQLSyntax, SQLBuiltQuery}
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.Row
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
