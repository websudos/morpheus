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
import com.websudos.morpheus.column.{AbstractColumn, DefaultForeignKeyConstraints}
import com.websudos.morpheus.query.AbstractQueryColumn

/**
 * As the implementation of SQL builders may differ depending on the type of SQL database in use, we will provide a series of specific imports for each
 * individual database.
 *
 * For instance, for MySQL a user will {{ import com.websudos.morpheus.mysql._ }}, for Postgress the user will {{import com.websudos.morpheus
 * .postgres._ }} and so on. To make our life easy when we reach the point of writing disjoint import objects for the various SQL databases,
 * this trait will provide the base implementation of an "all you can eat" imports object.
 *
 * This includes the various conversions and implicit mechanisms which will have there own underlying structure. As any underlying variable implementation
 * between databases will still extend the same base implementations internally, we can easily override with the settings we need in any place we want.
 *
 * Thanks to Scala's ability to override the below type aliases, we can easily swap out the base BaseTable implementation for a MySQL specific BaseTable
 * implementation in a manner that's completely invisible to the API user. The naming of things can stay the same while morpheus invisibly implements all
 * necessary discrepancies.
 */
trait DefaultImportsDefinition extends DefaultForeignKeyConstraints {

  type SQLPrimitive[T] = com.websudos.morpheus.SQLPrimitive[T]

  type BaseTable[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: Row] = com.websudos.morpheus.dsl.BaseTable[Owner, Record, TableRow]


  type PrimaryKey[ValueType] = com.websudos.morpheus.keys.PrimaryKey[ValueType]
  type UniqueKey[ValueType] = com.websudos.morpheus.keys.UniqueKey[ValueType]
  type NotNull = com.websudos.morpheus.keys.NotNull
  type Autoincrement = com.websudos.morpheus.keys.Autoincrement
  type Zerofill[ValueType] = com.websudos.morpheus.keys.Zerofill[ValueType]

  implicit def columnToQueryColumn[T: SQLPrimitive](col: AbstractColumn[T]): AbstractQueryColumn[T]

}

