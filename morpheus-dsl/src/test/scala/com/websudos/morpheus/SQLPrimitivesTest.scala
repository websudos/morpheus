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
package com.websudos.morpheus

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.websudos.morpheus.sql._

class SQLPrimitivesTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "The SQL String primitive" should "always use '(apostrophes) around the serialised strings" in {
    forAll(minSuccessful(300)) { (name: String) =>
      whenever (name.length > 0) {
        val query = implicitly[SQLPrimitive[String]].toSQL(name)
        query shouldEqual s"'$name'"
      }
    }
  }

  "The SQL Long primitive" should "serialise a Long value to its string value" in {
    forAll(minSuccessful(300)) { (value: Long) =>
      val query = implicitly[SQLPrimitive[Long]].toSQL(value)
      query shouldEqual s"${value.toString}"
    }
  }

  "The SQL Int primitive" should "serialise a Int value to its string value" in {
    forAll(minSuccessful(300)) { (value: Int) =>
      val query = implicitly[SQLPrimitive[Int]].toSQL(value)
      query shouldEqual s"${value.toString}"
    }
  }


}
