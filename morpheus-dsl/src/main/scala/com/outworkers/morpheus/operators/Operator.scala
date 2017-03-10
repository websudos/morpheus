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
package com.outworkers.morpheus.operators

import com.outworkers.morpheus.{Row, DataType}
import com.outworkers.morpheus.engine.query.{AssignBind, ChainBind}
import com.outworkers.morpheus.builder.DefaultQueryBuilder
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.engine.query._
import shapeless.HNil

sealed abstract class Operator

sealed class AsciiOperator extends Operator {

  final def apply[T](value: T)(implicit ev: DataType[T], ev2: DataType[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.ascii(implicitly[DataType[T]].serialize(value))
    ) {
      def fromRow(row: Row): String = ""
    }
  }
}



sealed class BitLengthOperator extends Operator {
  final def apply[T](value: T)(implicit ev: DataType[T], ev2: DataType[Int]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.bitLength(implicitly[DataType[T]].serialize(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class CharLengthOperator extends Operator {
  final def apply[T](value: T)(implicit ev: DataType[T], ev2: DataType[Int]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.charLength(implicitly[DataType[T]].serialize(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class CharacterLengthOperator extends Operator {
  final def apply[T](value: T)(implicit ev: DataType[T], ev2: DataType[Int]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.characterLength(implicitly[DataType[T]].serialize(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class ConcatWsOperator extends Operator {

  final def apply(values: List[String])(implicit ev: DataType[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concatWs(values.map(implicitly[DataType[String]].serialize))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply(sep: String, values: List[String])(implicit ev: DataType[String]): SelectOperatorClause[String] = {

    val primitive = implicitly[DataType[String]]

    new SelectOperatorClause[String](
      DefaultQueryBuilder.concatWs(primitive.serialize(sep) +: values.map(primitive.serialize))
    ) {
      def fromRow(row: Row): String = ""
    }
  }
}

sealed class ConcatOperator extends Operator {

  final def apply(values: List[String])(implicit ev: DataType[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concat(values.map(implicitly[DataType[String]].serialize))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply(values: String*)(implicit ev: DataType[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concat(values.map(implicitly[DataType[String]].serialize).toList)
    ) {
      override def fromRow(row: Row): String = ""
    }
  }
}


sealed class ExistsOperator extends Operator {

  final def apply[
    T <: BaseTable[T, R, TableRow],
    R,
    TableRow <: Row,
    Group <: GroupBind,
    Order <: OrderBind,
    Limit <: LimitBind,
    Chain <: ChainBind,
    AssignChain <: AssignBind
  ](query: SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, HNil]): QueryCondition = {
    QueryCondition(
      query.table.queryBuilder.exists(query.query)
    )
  }
}


sealed class NotExistsOperator extends Operator {

  final def apply[
    T <: BaseTable[T, R, TableRow],
    R,
    TableRow <: Row,
    Group <: GroupBind,
    Order <: OrderBind,
    Limit <: LimitBind,
    Chain <: ChainBind,
    AssignChain <: AssignBind
  ](query: SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, HNil]): QueryCondition = {
    QueryCondition(
      query.table.queryBuilder.notExists(query.query)
    )
  }
}

sealed class IntervalOperator extends Operator {

  final def apply[T](values: List[T])(implicit ev: DataType[T], ev2: DataType[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.interval(values.map(implicitly[DataType[T]].serialize))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply[T](values: T*)(implicit ev: DataType[T], ev2: DataType[String]): SelectOperatorClause[String] = {
    apply(values.toList)
  }
}

sealed class LeastOperator extends Operator {
  final def apply[T](values: List[T])(implicit ev: DataType[T], ev2: DataType[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.least(values.map(implicitly[DataType[T]].serialize))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply[T](values: T*)(implicit ev: DataType[T], ev2: DataType[String]): SelectOperatorClause[String] = {
    apply(values.toList.map(implicitly[DataType[T]].serialize))
  }
}

sealed class GreatestOperator extends Operator {
  final def apply[T](values: List[T])(implicit ev: DataType[T], ev2: DataType[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.greatest(values.map(implicitly[DataType[T]].serialize))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply[T](values: T*)(implicit ev: DataType[T], ev2: DataType[String]): SelectOperatorClause[String] = {
    apply(values.toList.map(implicitly[DataType[T]].serialize))
  }
}

sealed class CoalesceOperator extends Operator {

  final def apply[T](values: List[T])(implicit ev: DataType[T], ev2: DataType[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.greatest(values.map(implicitly[DataType[T]].serialize))
    ) {
      def fromRow(row: Row): String = ""
    }
  }
}

sealed class BinOperator extends Operator {
  final def apply[T](value: T)(implicit ev: DataType[T], ev2: DataType[Int], ev3: Numeric[T]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.bin(ev.serialize(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

/**
 * The default collection of SQL Syntax operators as defined by the SQL standard.
 * These operators are always applied in-flight, they are not and cannot be chained to other objects.
 *
 * This structure allows implementors to provide completely different sets of in flight operators depending on the database in use.
 * The default set can be easily overriden and even the underlying implementation of the operator functions can be overriden by each database specific implementation.
 */
private[morpheus] trait SQLOperatorSet {
  object ascii extends AsciiOperator
  object bin extends BinOperator
  object bitLength extends BitLengthOperator
  object charLength extends CharLengthOperator
  object characterLength extends CharacterLengthOperator
  object concatWs extends ConcatWsOperator
  object concat extends ConcatOperator

  object exists extends ExistsOperator
  object notExists extends NotExistsOperator
  object interval extends IntervalOperator
  object least extends LeastOperator
  object greatest extends GreatestOperator
}


trait MySQLOperatorSet extends SQLOperatorSet {}
