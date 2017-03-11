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
package com.outworkers.morpheus.keys

import com.outworkers.morpheus.column.NumericColumn
import com.outworkers.morpheus.builder.{DefaultSQLSyntax, SQLBuiltQuery}
import com.outworkers.morpheus.column.AbstractColumn

private[morpheus] trait Key[KeyType <: Key[KeyType]] { self: AbstractColumn[_] =>

  override def qb: SQLBuiltQuery = {
    SQLBuiltQuery(name).pad.append(sqlType)
      .forcePad.append(keyToQueryString)
      .pad.append(notNull match {
      case true => DefaultSQLSyntax.notNull
      case false => ""
    }).pad.append(autoIncrement match {
      case true => DefaultSQLSyntax.autoIncrement
      case false => ""
    }).trim
  }


  protected[this] def keyToQueryString: String
  protected[this] val autoIncrement = false
}

trait PrimaryKey extends Key[PrimaryKey] {
  self: AbstractColumn[_] =>

  protected[this] def keyToQueryString = DefaultSQLSyntax.primaryKey
}

trait UniqueKey extends Key[UniqueKey] {
  self: AbstractColumn[_] =>

  protected[this] def keyToQueryString = DefaultSQLSyntax.uniqueKey

}

trait NotNull { self: AbstractColumn[_] =>

  override def notNull: Boolean = true
}

trait Autoincrement { self: AbstractColumn[Int] with PrimaryKey =>

  override protected[this] val autoIncrement = true
}

trait Zerofill extends Key[Zerofill] { self: NumericColumn[_, _, _, _] =>

  override def qb: SQLBuiltQuery = {
    SQLBuiltQuery(name).pad.append(sqlType)
      .forcePad.append(keyToQueryString)
      .pad.append(unsigned match {
        case true => DefaultSQLSyntax.unsigned
        case false => ""
      })
      .pad.append(notNull match {
        case true => DefaultSQLSyntax.notNull
        case false => ""
      }).pad.append(autoIncrement match {
        case true => DefaultSQLSyntax.autoIncrement
        case false => ""
      }).trim
  }

  protected[this] def keyToQueryString = DefaultSQLSyntax.zeroFill

}

trait Unsigned[ValueType] { self: NumericColumn[_, _, _, ValueType] =>

  override def unsigned: Boolean = true
}
