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

package com.websudos.morpheus.operators

import com.websudos.morpheus.builder.DefaultQueryBuilder
import com.websudos.morpheus.{Row, SQLPrimitive}
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.query._

sealed abstract class Operator {}

sealed class AsciiOperator extends Operator {

  final def apply[T](value: T)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.ascii(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): String = ""
    }
  }
}

sealed class BinOperator extends Operator {
  final def apply[T](value: T)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[Int], ev3: Numeric[T]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.bin(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class BitLengthOperator extends Operator {
  final def apply[T](value: T)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[Int]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.bitLength(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class CharLengthOperator extends Operator {
  final def apply[T](value: T)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[Int]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.charLength(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class CharacterLengthOperator extends Operator {
  final def apply[T](value: T)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[Int]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.characterLength(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class ConcatWsOperator extends Operator {

  final def apply(values: List[String])(implicit ev: SQLPrimitive[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concatWs(values.map(implicitly[SQLPrimitive[String]].toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply(sep: String, values: List[String])(implicit ev: SQLPrimitive[String]): SelectOperatorClause[String] = {

    val primitive = implicitly[SQLPrimitive[String]]

    new SelectOperatorClause[String](
      DefaultQueryBuilder.concatWs(primitive.toSQL(sep) +: values.map(primitive.toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }
}

sealed class ConcatOperator extends Operator {

  final def apply(values: List[String])(implicit ev: SQLPrimitive[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concat(values.map(implicitly[SQLPrimitive[String]].toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply(values: String*)(implicit ev: SQLPrimitive[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concat(values.map(implicitly[SQLPrimitive[String]].toSQL).toList)
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
  ](query: SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated]): QueryCondition = {
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
  ](query: SelectQuery[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Unterminated]): QueryCondition = {
    QueryCondition(
      query.table.queryBuilder.notExists(query.query)
    )
  }
}

sealed class IntervalOperator extends Operator {

  final def apply[T](values: List[T])(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.interval(values.map(implicitly[SQLPrimitive[T]].toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply[T](values: T*)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[String]): SelectOperatorClause[String] = {
    apply(values.toList)
  }
}

sealed class LeastOperator extends Operator {
  final def apply[T](values: List[T])(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.least(values.map(implicitly[SQLPrimitive[T]].toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply[T](values: T*)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[String]): SelectOperatorClause[String] = {
    apply(values.toList.map(implicitly[SQLPrimitive[T]].toSQL))
  }
}

sealed class GreatestOperator extends Operator {
  final def apply[T](values: List[T])(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.greatest(values.map(implicitly[SQLPrimitive[T]].toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply[T](values: T*)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[String]): SelectOperatorClause[String] = {
    apply(values.toList.map(implicitly[SQLPrimitive[T]].toSQL))
  }
}

sealed class CoalesceOperator extends Operator {

  final def apply[T](values: List[T])(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.greatest(values.map(implicitly[SQLPrimitive[T]].toSQL))
    ) {
      def fromRow(row: Row): String = ""
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
