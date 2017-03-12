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
package com.outworkers.morpheus.mysql

import com.twitter.finagle.exp.mysql.{ Field, Value, Row => FinagleRow }

class EmptyRow(fn: String => Option[Value]) extends FinagleRow {
  override val fields: IndexedSeq[Field] = IndexedSeq.empty[Field]
  override val values: IndexedSeq[Value] = IndexedSeq.empty[Value]

  override def apply(columnName: String): Option[Value] = fn(columnName)

  override def indexOf(columnName: String): Option[Int] = None
}
