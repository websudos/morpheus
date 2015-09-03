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

import java.util.concurrent.{Callable, Executors}

import scala.collection.JavaConversions._

import org.scalatest.FunSuite

import com.websudos.morpheus.sql._

class RaceConditionTest extends FunSuite {

  case class Group(id: Long, name: String)

  class Groups extends Table[Groups, Group] {
    object id extends LongColumn(this)
    object name extends TextColumn(this)

    def fromRow(row: DefaultRow) = Group(id(row), name(row))
  }

  case class User(id: Long, name: String)

  class Users extends Table[Users, User] {
    object id extends LongColumn(this)
    object name extends TextColumn(this)

    def fromRow(row: DefaultRow) = User(id(row), name(row))
  }

  test("parallel tables instantiation") {
    val executor = Executors.newFixedThreadPool(2)
    val futureResults = executor.invokeAll(List(
      new Callable[AnyRef] {
        override def call() = new Users
      },
      new Callable[AnyRef] {
        override def call() = new Groups
      }
    ))
    futureResults.map(_.get())
  }

  test("sequential tables instantiation") {
    new Groups
    new Users
  }
}
