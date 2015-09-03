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
package com.websudos.morpheus.sql

import com.websudos.morpheus.column._
import shapeless.{<:!<, =:!=}

trait SqlKeys {

  abstract class ForeignKey[T <: BaseTable[T, R, DefaultRow], R, T1 <: BaseTable[T1, _, DefaultRow]]
  (origin: T, columns: IndexColumn#NonIndexColumn[T1]*)
  (implicit ev: T =:!= T1, ev2: IndexColumn#NonIndexColumn[T1] <:!< IndexColumn) extends AbstractForeignKey[T, R, DefaultRow, T1](origin, columns: _*)

  class Index[T <: BaseTable[T, R, DefaultRow], R](columns: IndexColumn#NonIndexColumn[_]*)(implicit ev:
    IndexColumn#NonIndexColumn[_] <:!< IndexColumn) extends AbstractIndex[T, R, DefaultRow](columns: _*)

}


trait SqlPrimitiveColumns {
  class LongColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow])(implicit ev: SQLPrimitive[Long])
    extends AbstractLongColumn[T, R, DefaultRow](t)
}


trait SqlColumns {

  class IntColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractIntColumn[T, R, DefaultRow](t, limit)

  class MediumIntColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractMediumIntColumn[T, R, DefaultRow](t, limit)

  class SmallIntColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractSmallIntColumn[T, R, DefaultRow](t, limit)

  class TinyIntColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractTinyIntColumn[T, R, DefaultRow](t, limit)

  class YearColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow])(implicit ev: SQLPrimitive[Int])
    extends AbstractYearColumn[T, R, DefaultRow](t)


  class BlobColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractBlobColumn[T, R, DefaultRow](t, limit)

  class LongTextColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = KnownTypeLimits.longTextLimit)(implicit ev: SQLPrimitive[String])
    extends AbstractLongTextColumn[T, R, DefaultRow](t, limit)

  class MediumBlobColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractMediumBlobColumn[T, R, DefaultRow](t, limit)

  class MediumTextColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = KnownTypeLimits.mediumTextLimit)(implicit ev: SQLPrimitive[String])
    extends AbstractMediumTextColumn[T, R, DefaultRow](t, limit)

  class TextColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = KnownTypeLimits.textLimit)(implicit ev: SQLPrimitive[String])
    extends AbstractTextColumn[T, R, DefaultRow](t)

  class TinyBlobColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTinyBlobColumn[T, R, DefaultRow](t, limit)

  class TinyTextColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTinyTextColumn[T, R, DefaultRow](t, limit)

  class VarcharColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = KnownTypeLimits.varcharLimit)(implicit ev: SQLPrimitive[String])
    extends AbstractVarcharColumn[T, R, DefaultRow](t, limit)

  class CharColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = KnownTypeLimits.charLimit)(implicit ev: SQLPrimitive[String])
    extends AbstractCharColumn[T, R, DefaultRow](t, limit)

  class LongBlobColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractLongBlobColumn[T, R, DefaultRow](t, limit)
}
