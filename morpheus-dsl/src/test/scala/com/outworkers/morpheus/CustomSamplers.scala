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
package com.outworkers.morpheus

import java.sql.{ Date => SqlDate }
import java.util.Date

import org.joda.time.{DateTime, DateTimeZone}
import org.scalacheck.{Arbitrary, Gen}

trait CustomSamplers {
  implicit val dateGen: Arbitrary[Date] = Arbitrary(Gen.delay(new Date(new DateTime(DateTimeZone.UTC).getMillis)))

  implicit val sqlDateGen: Arbitrary[SqlDate] = Arbitrary(Gen.delay(new SqlDate(new DateTime(DateTimeZone.UTC).getMillis)))

  implicit class JavaDateAug(val dt: Date) {
    def asSql: SqlDate = new SqlDate(dt.getTime)
  }
}
