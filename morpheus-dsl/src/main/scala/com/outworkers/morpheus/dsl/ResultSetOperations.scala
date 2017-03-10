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
package com.outworkers.morpheus.dsl

import scala.concurrent.{Future => ScalaFuture, Promise => ScalaPromise}

import com.twitter.util.{ Future, Throw, Return }
import com.outworkers.morpheus.{Row, Result, Client}

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
