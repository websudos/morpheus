package com.websudos.morpheus.mysql

import com.websudos.morpheus.mysql.tables.{TestEnumeration, EnumerationRecord}
import com.websudos.util.testing._

trait Samplers {
  implicit object EnumerationRecordSampler extends Sample[EnumerationRecord] {
    def sample: EnumerationRecord = {
      EnumerationRecord(
        id = gen[Int],
        enum = oneOf(TestEnumeration)
      )
    }
  }
}
