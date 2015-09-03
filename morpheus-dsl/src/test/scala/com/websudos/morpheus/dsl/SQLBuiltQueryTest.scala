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

import com.websudos.morpheus.builder.SQLBuiltQuery
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, FlatSpec}

class SQLBuiltQueryTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  it should "serialise an append on an SQLBuiltQuery" in {
    forAll(minSuccessful(300)) { (part1: String, part2: String) =>
      whenever (part1.length > 0 && part2.length > 0) {
        val query = SQLBuiltQuery(part1).append(SQLBuiltQuery(part2)).queryString
        query shouldEqual s"$part1$part2"
      }
    }
  }

  it should "serialise a prepend on an SQLBuiltQuery" in {
    forAll(minSuccessful(300)) { (part1: String, part2: String) =>
      whenever (part1.length > 0 && part2.length > 0) {
        val query = SQLBuiltQuery(part1).prepend(SQLBuiltQuery(part2)).queryString
        query shouldEqual s"$part2$part1"
      }
    }
  }


  it should "serialise and pad an SQLBuiltQuery with a trailing space if the space is missing" in {
    forAll(minSuccessful(300)) { (part1: String) =>
      whenever (!part1.isEmpty ) {
        val tested = part1.trim
        val query = SQLBuiltQuery(tested).pad.queryString
        query shouldEqual s"$tested "
      }
    }
  }

  it should "not add a trailing space if the last character of an SQLBuiltQuery is a space" in {
    forAll(minSuccessful(300)) { (part1: String) =>
      whenever (part1.length > 0 && !part1.endsWith(" ")) {
        val s = part1 + " "
        val query = SQLBuiltQuery(s).pad.queryString
        query shouldEqual s"$s"
      }
    }
  }

  it should "wrap a value in a set of parentheses" in {
    forAll(minSuccessful(300)) { (part1: String, value: String) =>
      whenever (part1.length > 0 && !part1.endsWith(" ") && !value.isEmpty) {
        val query = SQLBuiltQuery(part1).wrap(value).queryString
        query shouldEqual s"$part1 ($value)"
      }
    }
  }

}
