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
import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.query.QueryAssignment

import scala.reflect.runtime.{currentMirror => cm}
import scala.util.{Failure, Success, Try}

private[morpheus] trait SchemaSerializer {
  def qb: SQLBuiltQuery
}

trait AbstractColumn[@specialized(Int, Double, Float, Long, Boolean, Short) T] extends SchemaSerializer {

  type Value = T

  lazy val name: String = cm.reflect(this).symbol.name.toTypeName.decodedName.toString

  def qb: SQLBuiltQuery

  def sqlType: String

  def table: BaseTable[_, _, _]

  def toQueryString(v: T): String

  /**
   * This method, overridden by the Null and NotNull Key mixins, tells if a column is allowed to be null with respect to the SQL schema.
   * Using the stackable trait pattern it's easier for the end DSL users to mentally map a Scala schema to an SQL schema with 0 effort and inversely,
   * go from SQL to Morpheus DSL code.
   * @return A Boolean value.
   */
  def notNull: Boolean = false

}


private[morpheus] abstract class SelectColumn[T](val qb: SQLBuiltQuery) {
  def apply(r: Row): T

  def queryString: String = qb.queryString
}

private[morpheus] abstract class Column[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: Row, T](val table: BaseTable[Owner, Record, TableRow])
  extends AbstractColumn[T] {

  def optional(r: Row): Try[T]

  def apply(r: Row): T = optional(r) match {
    case Success(value) => value
    case Failure(ex) => {
      table.logger.error(ex.getMessage)
      throw ex
    }
  }
}

private[morpheus] abstract class OptionalColumn[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: Row, T](val table: BaseTable[Owner, Record, TableRow])
  extends AbstractColumn[Option[T]] {

  def optional(r: Row): Try[T]

  def apply(r: Row): Option[T] = optional(r).toOption
}

private[morpheus] abstract class AbstractModifyColumn[RR](col: AbstractColumn[RR]) {

  def setTo(value: RR): QueryAssignment = {
    QueryAssignment(col.table.queryBuilder.setTo(col.name, col.toQueryString(value)))
  }
}
