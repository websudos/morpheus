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
package com.outworkers.morpheus.dsl

import com.outworkers.morpheus.Row
import com.outworkers.morpheus.column.SelectColumn
import com.outworkers.morpheus.engine.query._


/**
 * This wonderfully bloated trait is the one where we provide all select methods and partial select methods.
 * Since partial select methods will return type-safe tuples but Scala cannot correctly infer the type for varargs and pass it along to create an adjacent
 * Tuple, we provide a long series of methods which allow partial selects of up to 22 fields, as that's the maximum number of fields a Scala tuple can have.
 * @tparam Owner The table owning the record.
 * @tparam Record The record type.
 */
private[morpheus] trait SelectTable[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row,
  RootSelectQuery[A <: BaseTable[A, _, TableRow], B] <: AbstractRootSelectQuery[A, B, TableRow],
  Block <: AbstractSelectSyntaxBlock
] {
  self: BaseTable[Owner, Record, TableRow] =>

  /**
   * This allows a table implementation targeting a specific database to specify it's own root select query.
   * It's used to allow variations in SELECT syntax operators and quantifiers.
   *
   * For instance, MySQL has DISTINCT ROW as a valid SELECT quantifier whereas Postgres doesn't.
   * This is part of the mechanism allowing for the invisible swap of features through a single import.
   * @param table The table object used.
   * @param block The select syntax block to use.
   * @param rowFunc The function mapping a record to a type safe user defined output.
   * @tparam A The type of the owner table.
   * @tparam B The type of the record.
   * @return A root select query implementation.
   */
  protected[this] def createRootSelect[A <: BaseTable[A, _, TableRow], B](table: A, block: Block, rowFunc: TableRow => B): RootSelectQuery[A, B]

  protected[this] def selectBlock(query: String, tableName: String, cols: List[String] = List("*")): Block

  /**
   * This is the SELECT * query, where the user won't specify any partial select.
   * @return An instance of a RootSelectQuery.
   */
  def select: RootSelectQuery[Owner, Record] = {
    createRootSelect[Owner, Record](
      this.asInstanceOf[Owner],
      selectBlock(syntax.select, tableName),
      fromRow
    )
  }

  /**
   * This is the SELECT column1 query, where a single column is specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1](f1: Owner => SelectColumn[T1]): RootSelectQuery[Owner, T1] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    def rowFunc(row: Row): T1 = c1(row)

    createRootSelect[Owner, T1](
      this.asInstanceOf[Owner],
      selectBlock(syntax.select, tableName, List(c1.queryString)),
      rowFunc
    )
  }

  /**
   * This is the SELECT column1 query, where a single column is specified to be partially selected.
   * @return An instance of a RootSelectQuery.

  def select[Return : SQLPrimitive](f1: Owner => SelectOperatorClause[Return]): RootSelectQuery[Owner, Record] = {

    val t = this.asInstanceOf[Owner]

    createRootSelect[Owner, Record](
      this.asInstanceOf[Owner],
      createSelectSyntaxBlock(syntax.select, tableName, List(queryBuilder.select(tableName, f1(t).clause).queryString)),
      fromRow
    )
  }   */

  /**
   * This is the SELECT column1 column2 query, where 2 columns are specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1, T2](f1: Owner => SelectColumn[T1], f2: Owner => SelectColumn[T2]): RootSelectQuery[Owner, (T1, T2)] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    val c2: SelectColumn[T2] = f2(t)
    def rowFunc(row: Row): (T1, T2) = c1(row) -> c2(row)

    createRootSelect[Owner, (T1, T2)](
      this.asInstanceOf[Owner],
      selectBlock(syntax.select, tableName, List(c1.queryString, c2.queryString)),
      rowFunc
    )
  }

  /**
   * This is the SELECT column1 column2 column3 query, where 3 columns are specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1, T2, T3](
    f1: Owner => SelectColumn[T1],
    f2: Owner => SelectColumn[T2],
    f3: Owner => SelectColumn[T3]
  ): RootSelectQuery[Owner, (T1, T2, T3)] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    val c2: SelectColumn[T2] = f2(t)
    val c3: SelectColumn[T3] = f3(t)

    def rowFunc(row: Row): (T1, T2, T3) = Tuple3(c1(row), c2(row), c3(row) )

    createRootSelect[Owner, (T1, T2, T3)](
      this.asInstanceOf[Owner],
      selectBlock(syntax.select, tableName, List(c1.queryString, c2.queryString, c3.queryString)),
      rowFunc
    )
  }

  /**
   * This is the SELECT column1 column2 column3 column4 query, where 4 columns are specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1, T2, T3, T4](
    f1: Owner => SelectColumn[T1],
    f2: Owner => SelectColumn[T2],
    f3: Owner => SelectColumn[T3],
    f4: Owner => SelectColumn[T4]
  ): RootSelectQuery[Owner, (T1, T2,
    T3, T4)] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    val c2: SelectColumn[T2] = f2(t)
    val c3: SelectColumn[T3] = f3(t)
    val c4: SelectColumn[T4] = f4(t)

    def rowFunc(row: Row): (T1, T2, T3, T4) = Tuple4(c1(row), c2(row), c3(row), c4(row))

    createRootSelect[Owner, (T1, T2, T3, T4)](
      this.asInstanceOf[Owner],
      selectBlock(syntax.select, tableName, List(c1.queryString, c2.queryString, c3.queryString, c4.queryString)),
      rowFunc
    )
  }

  /**
   * This is the SELECT column1 column2 column3 column4 query, where 4 columns are specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1, T2, T3, T4, T5](
    f1: Owner => SelectColumn[T1],
    f2: Owner => SelectColumn[T2],
    f3: Owner => SelectColumn[T3],
    f4: Owner => SelectColumn[T4],
    f5: Owner => SelectColumn[T5]
  ): RootSelectQuery[Owner, (T1, T2, T3, T4, T5)] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    val c2: SelectColumn[T2] = f2(t)
    val c3: SelectColumn[T3] = f3(t)
    val c4: SelectColumn[T4] = f4(t)
    val c5: SelectColumn[T5] = f5(t)

    def rowFunc(row: Row): (T1, T2, T3, T4, T5) = Tuple5(c1(row), c2(row), c3(row), c4(row), c5(row))

    createRootSelect[Owner, (T1, T2, T3, T4, T5)](
      this.asInstanceOf[Owner],
      selectBlock(
        syntax.select,
        tableName,
        List(c1.queryString, c2.queryString, c3.queryString, c4.queryString, c5.queryString)
      ),
      rowFunc
    )
  }
}
