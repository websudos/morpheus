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

case class SQLBuiltQuery(queryString: String) {
  def instance(st: String): SQLBuiltQuery = SQLBuiltQuery(st)

  val defaultSep = ", "

  def nonEmpty: Boolean = queryString.nonEmpty

  def append(st: String): SQLBuiltQuery = instance(queryString + st)
  def append(st: SQLBuiltQuery): SQLBuiltQuery = append(st.queryString)

  def append[M[X] <: TraversableOnce[X]](list: M[String], sep: String = defaultSep): SQLBuiltQuery = {
    instance(queryString + list.mkString(sep))
  }

  def appendEscape(st: String): SQLBuiltQuery = append(escape(st))
  def appendEscape(st: SQLBuiltQuery): SQLBuiltQuery = appendEscape(st.queryString)

  def terminate: SQLBuiltQuery = appendIfAbsent(";")

  def appendSingleQuote(st: String): SQLBuiltQuery = append(singleQuote(st))
  def appendSingleQuote(st: SQLBuiltQuery): SQLBuiltQuery = append(singleQuote(st.queryString))

  def appendIfAbsent(st: String): SQLBuiltQuery = if (queryString.endsWith(st)) instance(queryString) else append(st)
  def appendIfAbsent(st: SQLBuiltQuery): SQLBuiltQuery = appendIfAbsent(st.queryString)

  def prepend(st: String): SQLBuiltQuery = instance(st + queryString)
  def prepend(st: SQLBuiltQuery): SQLBuiltQuery = prepend(st.queryString)

  def prependIfAbsent(st: String): SQLBuiltQuery = if (queryString.startsWith(st)) instance(queryString) else prepend(st)
  def prependIfAbsent(st: SQLBuiltQuery): SQLBuiltQuery = prependIfAbsent(st.queryString)

  def escape(st: String): String = "`" + st + "`"
  def singleQuote(st: String): String = "'" + st.replaceAll("'", "''") + "'"

  def spaced: Boolean = queryString.endsWith(" ")
  def pad: SQLBuiltQuery = appendIfAbsent(" ")
  def bpad: SQLBuiltQuery = prependIfAbsent(" ")

  def forcePad: SQLBuiltQuery = instance(queryString + " ")
  def trim: SQLBuiltQuery = instance(queryString.trim)

  def wrapn(str: String): SQLBuiltQuery = append("(").append(str).append(")")
  def wrapn(query: SQLBuiltQuery): SQLBuiltQuery = wrapn(query.queryString)
  def wrap(str: String): SQLBuiltQuery = pad.wrapn(str)
  def wrap(query: SQLBuiltQuery): SQLBuiltQuery = wrap(query.queryString)

  def wrapn[M[X] <: TraversableOnce[X]](
    col: M[String],
    sep: String = defaultSep
  ): SQLBuiltQuery = wrapn(col.mkString(sep))

  def wrap[M[X] <: TraversableOnce[X]](
    col: M[String],
    sep: String = defaultSep
  ): SQLBuiltQuery = wrap(col.mkString(sep))

  def wrapEscape(list: List[String]): SQLBuiltQuery = wrap(list.map(escape).mkString(", "))
}

object SQLBuiltQuery {
  def empty: SQLBuiltQuery = SQLBuiltQuery("")
}
