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

package com.websudos.morpheus.dsl

import com.websudos.morpheus.Row
import com.websudos.morpheus.builder.{AbstractQueryBuilder, AbstractSQLSyntax}
import com.websudos.morpheus.column.AbstractColumn
import com.websudos.morpheus.query._
import org.slf4j.LoggerFactory

import scala.collection.mutable.{ArrayBuffer => MutableArrayBuffer, SynchronizedBuffer => MutableSyncBuffer}
import scala.reflect.runtime.universe.Symbol
import scala.reflect.runtime.{currentMirror => cm, universe => ru}

/**
 * The basic wrapper definition of an SQL table. This will force greedy initialisation of all column object members and provide a way to map
 * the basic DSL table definition into a user defined Scala case class.
 *
 * Greedy object initialisation is done in a thread safe way via a global lock acquired on a singleton case object,
 * preventing race conditions on multiple threads accessing the same table during initialisation.
 *
 * Unlike the Java reflection API, which cannot guarantee any ordering in a compiled bytecode file of object members,
 * the Scala reflection API is capable of extracting objects in the order the user writes them inside a table definition.
 * This is a very important guarantee allowing for consistency throughout the DSL and auto-generating table schemas where order is important.
 *
 * @tparam Owner The Owner type of the table, pointing to the extending class defining a table. Used as follows: class SomeTable extends Table[SomeTable,
 *               SomeRecord]
 * @tparam Record The user defined Scala class, usually a case class, holding a type safe data model definition. This allows for type safe querying of
 *                records, as all select all queries will return an instance of Record.
 */
abstract class BaseTable[Owner <: BaseTable[Owner, _, TableRow], Record, TableRow <: Row] {

  val queryBuilder: AbstractQueryBuilder

  protected[this] def syntax: AbstractSQLSyntax


  lazy val logger = LoggerFactory.getLogger(getClass.getName.stripSuffix("$"))

  /**
   * This is a Synchronized mutable buffer allowing us to store references to the objects a user writes inside a table definition to represent columns.
   * Using f-bounded polymorphism and a simple synchronised collection, we can guarantee both type-safety with the normal guaurantees of a function container
   * type as well as thread safety when multiple threads are reading columns during synchronisation.
   *
   * The reading of columns doesn't used the same lock acquired by the initialisation of columns to guarantee columns are never read before initialisation
   * because this is made impossible at an API level later on.
   */
  private[this] lazy val _columns: MutableArrayBuffer[AbstractColumn[_]] = new MutableArrayBuffer[AbstractColumn[_]]

  /**
   * It allows DSL users to obtain good "default" values for their table names.
   *
   * As we don't want ugly looking strings bleeding all over our DSL like they do in Slick, a user can easily override the name of a table as follows:
   * <pre>
   * {@code class MyTable extends Table[MyTable, MyRecord {
   *   override val tableName = "custom"
   * }
   * }
   * </pre>
   *
   * The default name in the above case would have been "MyTable".
   */
  private[this] lazy val _name: String = {
    cm.reflect(this).symbol.name.toTypeName.decodedName.toString
  }

  /**
   * The most notable and honorable of functions in this file, this is what allows our DSL to provide type-safety.
   * It works by requiring a user to define a type-safe mapping between a buffered Result and the above refined Record.
   *
   * Objects delimiting pre-defined columns also have a pre-defined "apply" method, allowing the user to simply autofill the type-safe mapping by using
   * pre-existing definitions.
   *
   * @param row The row incoming as a result from a MySQL query.
   * @return A Record instance.
   */
  def fromRow(row: TableRow): Record

  def tableName: String = _name

  def create: RootCreateQuery[Owner, Record, TableRow]

  def update: RootUpdateQuery[Owner, Record, TableRow]

  def delete: RootDeleteQuery[Owner, Record, TableRow]

  def insert: RootInsertQuery[Owner, Record, TableRow]

  def columns: List[AbstractColumn[_]] = _columns.toList

  Lock.synchronized {
    val instanceMirror = cm.reflect(this)
    val selfType = instanceMirror.symbol.toType

    // Collect all column definitions starting from base class
    val columnMembers = MutableArrayBuffer.empty[Symbol]
    selfType.baseClasses.reverse.foreach {
      baseClass =>
        val baseClassMembers = baseClass.typeSignature.members.sorted
        val baseClassColumns = baseClassMembers.filter(_.typeSignature <:< ru.typeOf[AbstractColumn[_]])
        baseClassColumns.foreach(symbol => if (!columnMembers.contains(symbol)) columnMembers += symbol)
    }

    columnMembers.foreach {
      symbol =>
        val column = instanceMirror.reflectModule(symbol.asModule).instance
        _columns += column.asInstanceOf[AbstractColumn[_]]
    }
  }
}



private[morpheus] case object Lock
