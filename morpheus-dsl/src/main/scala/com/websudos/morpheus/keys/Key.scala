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

package com.websudos.morpheus.keys

import com.websudos.morpheus.builder.{SQLBuiltQuery, DefaultSQLSyntax}
import com.websudos.morpheus.column.{NumericColumn, AbstractColumn}

private[morpheus] trait Key[ValueType, KeyType <: Key[ValueType, KeyType]] {
  self: AbstractColumn[ValueType] =>

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

trait PrimaryKey[ValueType] extends Key[ValueType, PrimaryKey[ValueType]] {
  self: AbstractColumn[ValueType] =>

  protected[this] def keyToQueryString = DefaultSQLSyntax.primaryKey
}

trait UniqueKey[ValueType] extends Key[ValueType, UniqueKey[ValueType]] {
  self: AbstractColumn[ValueType] =>

  protected[this] def keyToQueryString = DefaultSQLSyntax.uniqueKey

}

trait NotNull {
  self: AbstractColumn[_] =>

  override val notNull = true
}

trait Autoincrement {
  self: Key[Int, _] with AbstractColumn[Int] =>

  override protected[this] val autoIncrement = true
}

trait Zerofill[ValueType] extends Key[ValueType, Zerofill[ValueType]] {
  self: NumericColumn[_, _, _, ValueType] =>

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

trait Unsigned[ValueType] {
  self: NumericColumn[_, _, _, ValueType] =>

  override def unsigned = true
}
