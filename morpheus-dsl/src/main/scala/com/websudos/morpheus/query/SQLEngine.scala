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
package com.websudos.morpheus.query

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
