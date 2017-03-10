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
package com.outworkers.morpheus.mysql.query.parts

import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.engine.query.parts.MergeList
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class PartsTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  it should "evaluate nonEmpty to true on a merge list of the inner list is empty" in {
    forAll { l: List[String] =>
      val part = MergeList(l.map(SQLBuiltQuery.apply))
      part.nonEmpty shouldEqual l.nonEmpty
    }
  }
}
