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

import com.outworkers.morpheus.Row
import com.outworkers.morpheus.builder.{SQLBuiltQuery, DefaultSQLSyntax, DefaultSQLDataTypes}
import com.outworkers.morpheus.dsl.BaseTable
import shapeless.{<:!<, =:!=}

import scala.annotation.implicitNotFound

/**
 * This is a simple mechanism of providing a pre-defined set of FOREIGN KEY constraints.
 * The available values currently follow the MySQL syntax definition available {@link http://dev.mysql.com/doc/refman/5.6/en/create-table-foreign-keys.html}.
 *
 * What follows below is actually the implementation of an Enumeration, however `scala.util.Enum` is not proffered in situations like this,
 * or ever for that matter. The API is poor and weird compared to using a simple set of `case object` definitions.
 * @param value String
 */
sealed class ForeignKeyConstraint(val value: String)


/**
 * This trait encloses all default variations of a FOREIGN KEY constraint as per the MySQL documentation.
 * It is a trait so that the default set of MySQL imports can mix it in to propagate the values and make them available to the end DSL user without anymore
 * work.
 * This approach will be standard throughout our DSL and it can be seen in use for things like available SQL engines and so on.
 */
private[morpheus] trait DefaultForeignKeyConstraints {
  case object Restrict extends ForeignKeyConstraint(DefaultSQLSyntax.restrict)
  case object Cascade extends ForeignKeyConstraint(DefaultSQLSyntax.cascade)
  case object SetNull extends ForeignKeyConstraint(DefaultSQLSyntax.setNull)
  case object NoAction extends ForeignKeyConstraint(DefaultSQLSyntax.noAction)
}

private[morpheus] object DefaultForeignKeyConstraints extends DefaultForeignKeyConstraints

/**
 * This apparently fruitless trait is used to enforce a type bound restriction in other parts of the DSL where the tables are not known.
 * It's a simple way of verifying that the column being dealt with is a ForeignKey and nothing else. For instance,
 * this is used when building join queries to ensure joins are properly created from a foreign key to the respective indexes.
 */
private[morpheus] trait ForeignKeyDefinition

/**
 * This is the implementation of a ForeignKey column. This is not a value column, therefore the `apply` method is overridden to throw an exception. It is used
 * at reflection time and schema generation time to correctly create the schema for a given table and serialise a ForeignKey accordingly.
 *
 * The peculiar type signature is very simple really. It's using the all known and loved shapeless type inequality constraint,
 * essentially forcing DSL users to specify 2 different owning tables for the origin and reference of a FOREIGN_KEY. It's a way of making FOREIGN_KEY
 * indexes impossible between a table and itself and it's also nicely confining all columns to belong to the same reference table,
 * effectively enforcing the SQL constraint that a ForeignKey cannot reference columns from more than one table.
 *
 * The second type constraint enforced via the ev2 implicit parameter is requesting that the columns referenced in a ForeignKey are not an Index or
 * ForeignKey themselves, as this is invalid with respect to SQL syntax.
 *
 * By default the action performed is DefaultForeignKeyConstraints.NoAction, with respect to the SQL standard.
 *
 * @param origin The table owning the foreign key.
 * @param columns The columns this foreign key references.
 * @tparam T The type of the owner table.
 * @tparam R The type of the record.
 */
@implicitNotFound("You are trying to define a ForeignKey from a table to its own columns or you are trying to define a relationship between this ForeignKey " +
  "and another ForeignKey or Index.")
abstract class AbstractForeignKey[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row,
  T1 <: BaseTable[T1, _, TableRow]
](origin: T, columns: IndexColumn#NonIndexColumn[T1]*)
  (implicit ev: T =:!= T1, ev2: IndexColumn#NonIndexColumn[T1] <:!< IndexColumn)

  extends AbstractColumn[String] with IndexColumn with ForeignKeyDefinition {

  private[this] val refTable: BaseTable[_, _, _] = columns.headOption.map(_.table).orNull

  def qb: SQLBuiltQuery = {
    val default = SQLBuiltQuery(DefaultSQLSyntax.foreignKey)
      .forcePad.append(DefaultSQLSyntax.`(`)
      .append(columns.map(col => {s"${refTable.tableName}_${col.name}"}).mkString(", "))
      .append(DefaultSQLSyntax.`)`)
      .forcePad.append(DefaultSQLSyntax.references)
      .forcePad.append(refTable.tableName)
      .append(DefaultSQLSyntax.`(`)
      .append(columns.map(_.name).mkString(", "))
      .append(DefaultSQLSyntax.`)`)

    val stage2 = if (onUpdate != DefaultForeignKeyConstraints.NoAction) {
      default.forcePad.append(DefaultSQLSyntax.onUpdate).forcePad.append(onUpdate.value)
    } else {
      default
    }

    val stage3 = if (onDelete != DefaultForeignKeyConstraints.NoAction) {
      stage2.forcePad.append(DefaultSQLSyntax.onDelete).forcePad.append(onDelete.value)
    } else {
      stage2
    }
    stage3
  }

  /**
   * This is actually irrelevant at any further point, since the query builder of ForeignKey will not account for its SQL type.
   * We do however need to satisfy the context bound of an SQL primitive and we do so easily but using a random predefined type.
   *
   * @return The SQL type of the column, ignored in the current context.
   */
  override def sqlType: String = DefaultSQLDataTypes.text


  /**
   * Same story as above, these are placeholders to satisfy the structure we've defined for our DSL.
   * It is likely proof we could have decoupled certain things to prevent the need for impromptu definitions like this, but we didn't so far.
   * @param v The value to convert to an SQL value.
   * @return
   */
  override def toQueryString(v: String): String = v

  /**
   * A dangerous mix indeed, since it will all go to hell if the user defines a ForeignKey constraint to no column.
   * The above root cause of evil for the below line is not valid SQL syntax, but we should likely upgrade to a more sensible error.
   * TODO (flavian): Idiotic line, upgrade the preconditions.
   * @return
   */
  override def table: BaseTable[_, _, _] = origin

  /**
   * The default ForeignKey constraint with respect to the MySQL documentation is NoAction and we enforce that here.
   * If the constraint defined on either update or delete is NoAction, then there will be no serialisation output as MySQL doesn't need it.
   * @return The ForeignKey Constraint to execute on update.
   */
  def onUpdate: ForeignKeyConstraint = DefaultForeignKeyConstraints.NoAction

  def onDelete: ForeignKeyConstraint = DefaultForeignKeyConstraints.NoAction
}

