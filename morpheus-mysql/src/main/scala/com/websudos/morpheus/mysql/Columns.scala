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

package com.websudos.morpheus.mysql

import com.websudos.morpheus.column._
import shapeless.{<:!<, =:!=}

trait MySQLKeys {

  abstract class ForeignKey[T <: BaseTable[T, R, MySQLRow], R, T1 <: BaseTable[T1, _, MySQLRow]]
  (origin: T, columns: IndexColumn#NonIndexColumn[T1]*)
  (implicit ev: T =:!= T1, ev2: IndexColumn#NonIndexColumn[T1] <:!< IndexColumn) extends AbstractForeignKey[T, R, MySQLRow, T1](origin, columns: _*)


  class Index[T <: BaseTable[T, R, MySQLRow], R](columns: IndexColumn#NonIndexColumn[_]*)(implicit ev:
  IndexColumn#NonIndexColumn[_] <:!< IndexColumn) extends AbstractIndex[T, R, MySQLRow](columns: _*)

}

trait MySQLPrimitiveColumns {

  class IntColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractIntColumn[T, R, MySQLRow](t, limit)

  class SmallIntColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractSmallIntColumn[T, R, MySQLRow](t, limit)

  class TinyIntColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractTinyIntColumn[T, R, MySQLRow](t, limit)

  class MediumIntColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractMediumIntColumn[T, R, MySQLRow](t, limit)
}

trait MySQLColumns {

  class BlobColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractBlobColumn[T, R, MySQLRow](t, limit)

  class TextColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTextColumn[T, R, MySQLRow](t, limit)

  class LongTextColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractLongTextColumn[T, R, MySQLRow](t, limit)

  class LongBlobColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractLongBlobColumn[T, R, MySQLRow](t, limit)

  class MediumBlobColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractMediumBlobColumn[T, R, MySQLRow](t, limit)

  class MediumTextColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractMediumTextColumn[T, R, MySQLRow](t, limit)

  class TinyBlobColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTinyBlobColumn[T, R, MySQLRow](t, limit)

  class TinyTextColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTinyTextColumn[T, R, MySQLRow](t, limit)

  class VarcharColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractVarcharColumn[T, R, MySQLRow](t, limit)

  class CharColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractCharColumn[T, R, MySQLRow](t, limit)

  class EnumColumn[T <: BaseTable[T, R, MySQLRow], R, EnumType <: Enumeration](t: BaseTable[T, R, MySQLRow], enum: EnumType)(implicit ev: SQLPrimitive[String])
    extends AbstractEnumColumn[T, R, MySQLRow, EnumType](t, enum) {
  }

}
