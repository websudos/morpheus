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
package com.outworkers.morpheus.builder

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class SQLQueryTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  it should "create an empty CQL query using the empty method on the companion object" in {
    SQLBuiltQuery.empty.queryString shouldEqual ""
  }

  it should "correctly identify if a CQL query is empty" in {
    forAll {(q1: String) =>
      whenever(q1.nonEmpty) {
        SQLBuiltQuery(q1).nonEmpty shouldEqual true
      }
    }
  }

  it should "prepend one query to another using SQLBuiltQuery.prepend" in {
    forAll {(q1: String, q2: String) =>
      SQLBuiltQuery(q1).prepend(SQLBuiltQuery(q2)).queryString shouldEqual s"$q2$q1"
    }
  }

  it should "prepend a string to a SQLBuiltQuery using SQLBuiltQuery.prepend" in {
    forAll {(q1: String, q2: String) =>
      SQLBuiltQuery(q1).prepend(q2).queryString shouldEqual s"$q2$q1"
    }
  }

  it should "append one query to another using SQLBuiltQuery.append" in {
    forAll {(q1: String, q2: String) =>
      SQLBuiltQuery(q1).append(SQLBuiltQuery(q2)).queryString shouldEqual s"$q1$q2"
    }
  }

  it should "append a string to a SQLBuiltQuery using SQLBuiltQuery.append" in {
    forAll {(q1: String, q2: String) =>
      SQLBuiltQuery(q1).append(q2).queryString shouldEqual s"$q1$q2"
    }
  }

  it should "append an escaped string to a SQLBuiltQuery using SQLBuiltQuery.append" in {
    forAll {(q1: String, q2: String) =>
      SQLBuiltQuery(q1).appendEscape(q2).queryString shouldEqual s"$q1`$q2`"
    }
  }

  it should "append an escaped query to another using SQLBuiltQuery.append" in {
    forAll {(q1: String, q2: String) =>
      SQLBuiltQuery(q1).appendEscape(SQLBuiltQuery(q2)).queryString shouldEqual s"$q1`$q2`"
    }
  }

  it should "prepend a string to another if the first string doesn't end with the other" in {
    forAll {(q1: String, q2: String) =>
      if(!q1.startsWith(q2)) {
        SQLBuiltQuery(q1).prependIfAbsent(q2).queryString shouldEqual s"$q2$q1"
      } else {
        SQLBuiltQuery(q1).prependIfAbsent(q2).queryString shouldEqual s"$q1"
      }
    }
  }

  it should "escape a CQL query by surrounding it with ` pairs" in {
    forAll { q1: String =>
      SQLBuiltQuery.escape(q1) shouldEqual s"`$q1`"
    }
  }

  it should "correctly bpad a query if it doesn't end with a space" in {
    forAll { q1: String =>
      val q = SQLBuiltQuery(q1)
      if (q1.startsWith(" ")) {
        q.bpad.queryString shouldEqual q.queryString
      } else {
        q.bpad.queryString shouldEqual s" ${q.queryString}"
      }
    }
  }

  it should "correctly pad a query if it doesn't end with a space" in {
    forAll { q1: String =>
      val q = SQLBuiltQuery(q1)
      if (q1.endsWith(" ")) {
        q.pad.queryString shouldEqual q.queryString
      } else {
        q.pad.queryString shouldEqual s"${q.queryString} "
      }
    }
  }

  it should "correctly forcePad pad a query if it DOES end with a space" in {
    forAll { q1: String =>
      SQLBuiltQuery(q1).forcePad.queryString shouldEqual s"$q1 "
    }
  }

  it should "correctly trim a SQLBuiltQuery" in {
    forAll { q1: String =>
      SQLBuiltQuery(q1).trim.queryString shouldEqual s"${q1.trim}"
    }
  }

  it should "correctly identify if a query ends with a space" in {
    forAll { q1: String =>
      SQLBuiltQuery(q1).spaced shouldEqual q1.endsWith(" ")
    }
  }

  it should "single quote a CQL query by surrounding it with ' pairs" in {
    forAll { q1: String =>
      SQLBuiltQuery.empty.singleQuote(q1) shouldEqual s"'$q1'"
    }
  }

  it should "prepend a query to another if the first string doesn't end with the other" in {
    forAll {(q1: String, q2: String) =>
      if (!q1.startsWith(q2)) {
        SQLBuiltQuery(q1).prependIfAbsent(SQLBuiltQuery(q2)).queryString shouldEqual s"$q2$q1"
      } else {
        SQLBuiltQuery(q1).prependIfAbsent(SQLBuiltQuery(q2)).queryString shouldEqual s"$q1"
      }
    }
  }

  it should "append a string to another if the first string doesn't end with the other" in {
    forAll {(q1: String, q2: String) =>
      if(!q1.endsWith(q2)) {
        SQLBuiltQuery(q1).appendIfAbsent(q2).queryString shouldEqual s"$q1$q2"
      } else {
        SQLBuiltQuery(q1).appendIfAbsent(q2).queryString shouldEqual s"$q1"
      }
    }
  }

  it should "append a query to another if the first string doesn't end with the other" in {
    forAll {(q1: String, q2: String) =>
      if (!q1.endsWith(q2)) {
        SQLBuiltQuery(q1).appendIfAbsent(SQLBuiltQuery(q2)).queryString shouldEqual s"$q1$q2"
      } else {
        SQLBuiltQuery(q1).appendIfAbsent(SQLBuiltQuery(q2)).queryString shouldEqual s"$q1"
      }
    }
  }

  it should "append an single quoted string to a SQLBuiltQuery using SQLBuiltQuery.append" in {
    forAll {(q1: String, q2: String) =>
      SQLBuiltQuery(q1).appendSingleQuote(q2).queryString shouldEqual s"$q1'$q2'"
    }
  }

  it should "append an singlequoted query to another using SQLBuiltQuery.append" in {
    forAll {(q1: String, q2: String) =>
      SQLBuiltQuery(q1).appendSingleQuote(SQLBuiltQuery(q2)).queryString shouldEqual s"$q1'$q2'"
    }
  }

  it should "append a list of query strings to a SQLBuiltQuery" in {
    forAll {(q1: String, queries: List[String]) =>
      val qb = queries.mkString(", ")
      SQLBuiltQuery(q1).append(queries).queryString shouldEqual s"$q1$qb"
    }
  }

  it should "terminate a query with ; if it doesn't already end with a semi colon" in {
    forAll {(q1: String) =>
      whenever(!q1.endsWith(";")) {
        SQLBuiltQuery(q1).terminate.queryString shouldEqual s"$q1;"
      }
    }
  }

  it should "append and wrap a string with ()" in {
    forAll { (q1: String, q2: String) =>
      SQLBuiltQuery(q1).wrap(q2).queryString shouldEqual s"$q1 ($q2)"
    }
  }


  it should "append and wrap a SQLBuiltQuery with ()" in {
    forAll { (q1: String, q2: String) =>
      whenever(q2.nonEmpty) {
        SQLBuiltQuery(q1).wrap(SQLBuiltQuery(q2)).queryString shouldEqual s"$q1 ($q2)"
      }
    }
  }


  it should "append, pad and  wrap a list of query strings" in {
    forAll {(q1: String, queries: List[String]) =>
      val qb = queries.mkString(", ")
      SQLBuiltQuery(q1).wrap(queries).queryString shouldEqual s"$q1 ($qb)"
    }
  }

  it should "append, not pad and  wrap a list of query strings" in {
    forAll {(q1: String, queries: List[String]) =>
      val qb = queries.mkString(", ")
      SQLBuiltQuery(q1).wrapn(queries).queryString shouldEqual s"$q1($qb)"
    }
  }
}
