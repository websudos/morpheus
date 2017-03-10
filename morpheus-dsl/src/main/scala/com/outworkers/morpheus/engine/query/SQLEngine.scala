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
package com.outworkers.morpheus.engine.query

object DefaultMySQLEngines {
  val InnoDB = "InnoDB"
  val Memory = "MEMORY"
  val Heap = "HEAP"
  val Merge = "MERGE"
  val MrgMyLSAM = "MRG_MYISAM"
  val isam = "ISAM"
  val MrgISAM = "MRG_ISAM"
  val innoBase = "INNOBASE"
  val BDB = "BDB"
  val BerkleyDB = "BERKELEYDB"
  val NDBCluster = "NDBCLUSTER"
  val NDB = "NDB"
  val Example = "EXAMPLE"
  val Archive = "ARCHIVE"
  val CSV = "CSV"
  val Federated = "FEDERATED"
  val BlackHole = "BLACKHOLE"
}

sealed abstract class SQLEngine(val value: String)

/**
 * This is the sequence of default available storage engines in the MySQL 5.0 specification.
 * For the official documentation, @see <a href="http://dev.mysql.com/doc/refman/5.0/en/show-engines.html">the MySQL 5.0 docs</a>.
 *
 * More recent versions of MySQL features far less available options. The official list is available on @see <a href="http://dev.mysql.com/doc/refman/5
 * .7/en/show-engines.html">the MySQL 5.7 docs</a> page.
 */
trait DefaultSQLEngines {
  case object InnoDB extends SQLEngine(DefaultMySQLEngines.InnoDB)
  case object InnoBase extends SQLEngine(DefaultMySQLEngines.innoBase)
  case object Memory extends SQLEngine(DefaultMySQLEngines.Memory)
  case object Heap extends SQLEngine(DefaultMySQLEngines.Heap)
  case object Merge extends SQLEngine(DefaultMySQLEngines.Merge)
  case object BDB extends SQLEngine(DefaultMySQLEngines.BDB)
  case object BerkleyDB extends SQLEngine(DefaultMySQLEngines.BerkleyDB)
  case object NDBCluster extends SQLEngine(DefaultMySQLEngines.NDBCluster)
  case object NDB extends SQLEngine(DefaultMySQLEngines.NDB)
  case object Example extends SQLEngine(DefaultMySQLEngines.Example)
  case object Archive extends SQLEngine(DefaultMySQLEngines.Archive)
  case object CSV extends SQLEngine(DefaultMySQLEngines.CSV)
  case object Federated extends SQLEngine(DefaultMySQLEngines.Federated)
  case object Blackhole extends SQLEngine(DefaultMySQLEngines.BlackHole)

}

trait MySQLEngines extends DefaultSQLEngines {}
