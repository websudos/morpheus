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

import com.outworkers.morpheus.DataType
import com.outworkers.morpheus.builder.DefaultSQLDataTypes
import com.outworkers.morpheus.column._
import com.outworkers.morpheus.dsl.BaseTable
import shapeless.{<:!<, =:!=}

trait Keys {

  abstract class ForeignKey[
    T <: BaseTable[T, R, Row],
    R,
    T1 <: BaseTable[T1, _, Row]
  ](origin: T, columns: IndexColumn#NonIndexColumn[T1]*)(
    implicit ev: T =:!= T1,
    ev2: IndexColumn#NonIndexColumn[T1] <:!< IndexColumn
  ) extends AbstractForeignKey[T, R, Row, T1](origin, columns: _*)

  class Index[T <: BaseTable[T, R, Row], R](
    columns: IndexColumn#NonIndexColumn[_]*
  )(implicit ev: IndexColumn#NonIndexColumn[_] <:!< IndexColumn) extends AbstractIndex[T, R, Row](columns: _*)

}

trait PrimitiveColumns {

  class DoubleColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(
    implicit ev: DataType[Double]
  ) extends NumericColumn[T, R, Row, Double](t, limit) {
    override protected[this] def numericType: String = DataTypes.Real.double
  }

  class RealColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(
    implicit ev: DataType[Double]
  ) extends NumericColumn[T, R, Row, Double](t, limit) {
    override protected[this] def numericType: String = DataTypes.Real.double
  }

  class FloatColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(
    implicit ev: DataType[Float]
  ) extends NumericColumn[T, R, Row, Float](t, limit) {
    override protected[this] def numericType: String = DataTypes.Real.float
  }

  class LongColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(
    implicit ev: DataType[Long]
  ) extends NumericColumn[T, R, Row, Long](t, limit) {
    override protected[this] def numericType: String = DefaultSQLDataTypes.long
  }

  class IntColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[Int])
    extends AbstractIntColumn[T, R, Row](t, limit)

  class SmallIntColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[Int])
    extends AbstractSmallIntColumn[T, R, Row](t, limit)

  class TinyIntColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[Int])
    extends AbstractTinyIntColumn[T, R, Row](t, limit)

  class MediumIntColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[Int])
    extends AbstractMediumIntColumn[T, R, Row](t, limit)

  class DateColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(
    implicit ev: DataType[java.util.Date]
  ) extends AbstractDateColumn[T, R, Row](t)

  class DateTimeColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(
    implicit ev: DataType[org.joda.time.DateTime]
  ) extends AbstractDateTimeColumn[T, R, Row](t)

}

trait Columns {

  class BlobColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractBlobColumn[T, R, Row](t, limit)

  class TextColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractTextColumn[T, R, Row](t, limit)

  class LongTextColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractLongTextColumn[T, R, Row](t, limit)

  class LongBlobColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractLongBlobColumn[T, R, Row](t, limit)

  class MediumBlobColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractMediumBlobColumn[T, R, Row](t, limit)

  class MediumTextColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractMediumTextColumn[T, R, Row](t, limit)

  class TinyBlobColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractTinyBlobColumn[T, R, Row](t, limit)

  class TinyTextColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractTinyTextColumn[T, R, Row](t, limit)

  class VarcharColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractVarcharColumn[T, R, Row](t, limit)

  class CharColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractCharColumn[T, R, Row](t, limit)

  class EnumColumn[T <: BaseTable[T, R, Row], R, EnumType <: Enumeration](
    t: BaseTable[T, R, Row],
    enum: EnumType
  )(implicit ev: DataType[String])
    extends AbstractEnumColumn[T, R, Row, EnumType](t, enum) {
  }

}
