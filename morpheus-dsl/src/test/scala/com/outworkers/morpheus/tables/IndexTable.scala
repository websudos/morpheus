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
package com.outworkers.morpheus.tables

import com.outworkers.morpheus.keys.Unsigned
import com.outworkers.morpheus.sql._

case class IndexedRecord(id: Int, value: Long)

sealed class IndexTable extends Table[IndexTable, IndexedRecord] {

  object id extends SmallIntColumn(this) with PrimaryKey with NotNull with Autoincrement

  object value extends LongColumn(this)

  object index extends Index(id, value)

  def fromRow(row: Row): IndexedRecord = {
    IndexedRecord(
      id(row),
      value(row)
    )
  }
}

object IndexTable extends IndexTable

case class KeysRecord(id: Int)

sealed class KeysTable extends Table[KeysTable, KeysRecord] {

  object id extends IntColumn(this) with PrimaryKey

  object notNullId extends IntColumn(this) with PrimaryKey with NotNull

  object autoincrementedId extends IntColumn(this) with PrimaryKey with Autoincrement

  object indexId extends IntColumn(this) with PrimaryKey with NotNull with Autoincrement

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

  object uniqueIndex extends TextColumn(this) with UniqueKey
  object uniqueIndexNotNull extends TextColumn(this) with UniqueKey with NotNull



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
  override def fromRow(row: Row): KeysRecord = KeysRecord(id(row))
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
  object yearCol extends YearColumn(this)

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

  def fromRow(row: Row): String = textColumn(row)
}

object StringsTable extends StringsTable


class ZeroFillTable extends Table[ZeroFillTable, Int] {

  object tinyInt extends TinyIntColumn(this) with Zerofill with Unsigned[Int] with NotNull
  object tinyIntLimited extends TinyIntColumn(this, 5) with Zerofill with NotNull

  object smallInt extends SmallIntColumn(this) with Zerofill with Unsigned[Int]
  object smallIntLimited extends SmallIntColumn(this, 5)


  def fromRow(row: Row): Int = tinyInt(row)
}

object ZeroFillTable extends ZeroFillTable
