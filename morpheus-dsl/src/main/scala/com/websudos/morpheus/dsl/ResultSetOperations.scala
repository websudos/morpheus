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

import scala.concurrent.{Future => ScalaFuture, Promise => ScalaPromise}

import com.twitter.util.{ Future, Throw, Return }
import com.websudos.morpheus.{Row, Result, Client}

private[morpheus] trait ResultSetOperations {

  protected[this] def queryToFuture[DBRow <: Row, DBResult <: Result](query: String)(implicit client: Client[DBRow, DBResult]): Future[DBResult] = {
    client.query(query)
  }

  protected[this] def queryToScalaFuture[DBRow <: Row, DBResult <: Result](query: String)(implicit client: Client[DBRow, DBResult]): ScalaFuture[DBResult] = {
    twitterToScala(client.query(query))
  }

  protected[this] def twitterToScala[A](future: Future[A]): ScalaFuture[A] = {
    val promise = ScalaPromise[A]()
    future respond {
      case Return(data) => promise success data
      case Throw(err) => promise failure err
    }
    promise.future
  }

}
