package com.websudos.morpheus.mysql.db.specialized

import com.websudos.morpheus.mysql.{Samplers, _}
import com.websudos.morpheus.mysql.db.MySQLSuite
import com.websudos.morpheus.mysql.tables.{TestEnumeration, EnumerationRecord, EnumerationTable}
import com.websudos.util.testing._
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class EnumerationColumnTest extends FlatSpec with MySQLSuite with Samplers {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Await.result(EnumerationTable.create.ifNotExists.engine(InnoDB).future(), 5.seconds)
  }

  implicit val enumPrimitive: SQLPrimitive[TestEnumeration#Value] = {
    enumToQueryConditionPrimitive(TestEnumeration)
  }

  it should "store a record with an enumeration defined inside it" in {
    val record = gen[EnumerationRecord]

    val chain = for {
      store <- EnumerationTable.insert
        .value(_.id, record.id)
        .value(_.enum, record.enum)
        .future()
      get <- EnumerationTable.select.where(_.id eqs record.id).one
    } yield get

    whenReady(chain) {
      res => {
        res.value shouldEqual record
      }
    }

  }
}
