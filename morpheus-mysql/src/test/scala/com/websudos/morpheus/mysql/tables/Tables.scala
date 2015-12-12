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

package com.websudos.morpheus.mysql.tables

import com.websudos.morpheus.mysql._
import com.websudos.morpheus.mysql.query.MySQLInsertQuery
import com.websudos.morpheus.query.InsertQuery

import scala.concurrent.{ExecutionContext, Future}

case class IndexedRecord(id: Int, value: Long)

sealed class IndexTable extends Table[IndexTable, IndexedRecord] {

  object id extends SmallIntColumn(this) with PrimaryKey[Int] with NotNull with Autoincrement

  object value extends IntColumn(this)

  object index extends Index(id, value)

  def fromRow(row: MySQLRow): IndexedRecord = {
    IndexedRecord(
      id(row),
      value(row)
    )
  }
}

object IndexTable extends IndexTable

case class KeysRecord(id: Int)

sealed class KeysTable extends Table[KeysTable, KeysRecord] {

  object id extends IntColumn(this) with PrimaryKey[Int]

  object notNullId extends IntColumn(this) with PrimaryKey[Int] with NotNull

  object autoincrementedId extends IntColumn(this) with PrimaryKey[Int] with Autoincrement

  object indexId extends IntColumn(this) with PrimaryKey[Int] with NotNull with Autoincrement

  object foreignKey extends ForeignKey[KeysTable, KeysRecord, IndexTable](this, IndexTable.id, IndexTable.value)

  object foreignUpdateKey extends ForeignKey[KeysTable, KeysRecord, IndexTable](this, IndexTable.id, IndexTable.value) {
    override def onUpdate = Cascade
  }

  object foreignDeleteKey extends ForeignKey[KeysTable, KeysRecord, IndexTable](this, IndexTable.id, IndexTable.value) {
    override def onDelete = Cascade
  }

  object foreignFull extends ForeignKey[KeysTable, KeysRecord, IndexTable](this, IndexTable.id, IndexTable.value) {
    override def onUpdate = Cascade
    override def onDelete = Cascade
  }

  object foreignFullRestrict extends ForeignKey[KeysTable, KeysRecord, IndexTable](this, IndexTable.id, IndexTable.value) {
    override def onUpdate = Restrict
    override def onDelete = Restrict
  }

  object foreignFullRestrictSetNull extends ForeignKey[KeysTable, KeysRecord, IndexTable](this, IndexTable.id, IndexTable.value) {
    override def onUpdate = Restrict
    override def onDelete = SetNull
  }

  object uniqueIndex extends TextColumn(this) with UniqueKey[String]
  object uniqueIndexNotNull extends TextColumn(this) with UniqueKey[String] with NotNull

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
  override def fromRow(row: MySQLRow): KeysRecord = KeysRecord(id(row))
}

object KeysTable extends KeysTable


class NumericsTable extends Table[NumericsTable, Int] {

  object tinyInt extends TinyIntColumn(this)
  object tinyIntLimited extends TinyIntColumn(this, 100)

  object smallInt extends SmallIntColumn(this)
  object smallIntLimited extends SmallIntColumn(this, 100)

  object mediumInt extends MediumIntColumn(this)
  object mediumIntLimited extends MediumIntColumn(this, 100)

  object int extends IntColumn(this)
  object intLimited extends IntColumn(this, 100)

  def fromRow(row: Row): Int = int(row)
}

object NumericsTable extends NumericsTable


class StringsTable extends Table[StringsTable, String] {

  object charColumn extends CharColumn(this)
  object charLimited extends CharColumn(this, 100)

  object varChar extends VarcharColumn(this)
  object varCharLimited extends VarcharColumn(this, 100)

  object tinyText extends TinyTextColumn(this)
  object mediumText extends MediumTextColumn(this)
  object longText extends LongTextColumn(this)
  object textColumn extends TextColumn(this)

  object blobColumn extends BlobColumn(this)
  object tinyBlog extends TinyBlobColumn(this)
  object mediumBlob extends MediumBlobColumn(this)
  object largeBlob extends LongBlobColumn(this)

  def fromRow(row: MySQLRow): String = textColumn(row)
}

object StringsTable extends StringsTable

case class BasicRecord(name: String, count: Int)

class BasicTable extends Table[BasicTable, BasicRecord] {

  object name extends TextColumn(this)
  object count extends IntColumn(this)

  def fromRow(row: MySQLRow): BasicRecord = {
    BasicRecord(name(row), count(row))
  }

}

object BasicTable extends BasicTable


trait TestEnumeration extends Enumeration {
  val EnumOne = Value("One")
  val EnumTwo = Value("Two")
}

object TestEnumeration extends TestEnumeration

case class EnumerationRecord(
  id: Int,
  enum: TestEnumeration#Value
)

class EnumerationTable extends Table[EnumerationTable, EnumerationRecord] {

  object id extends IntColumn(this) with PrimaryKey[Int] with Autoincrement with NotNull

  object enum extends EnumColumn[EnumerationTable, EnumerationRecord, TestEnumeration](this, TestEnumeration)

  def fromRow(row: Row): EnumerationRecord = {
    EnumerationRecord(
      id = id(row),
      enum = enum(row)
    )
  }
}

object EnumerationTable extends EnumerationTable