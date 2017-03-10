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
import bintray.BintrayKeys._
import sbt.Keys._
import sbt._
import com.typesafe.sbt.pgp.PgpKeys._

import scala.util.Properties

object Publishing {

  val defaultPublishingSettings = Seq(
    version := "2.1.3"
  )

  lazy val noPublishSettings = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  lazy val defaultCredentials: Seq[Credentials] = {
    if (!Publishing.runningUnderCi) {
      Seq(
        Credentials(Path.userHome / ".bintray" / ".credentials"),
        Credentials(Path.userHome / ".ivy2" / ".credentials")
      )
    } else {
      Seq(
        Credentials(
          realm = "Bintray",
          host = "dl.bintray.com",
          userName = System.getenv("bintray_user"),
          passwd = System.getenv("bintray_password")
        ),
        Credentials(
          realm = "Sonatype OSS Repository Manager",
          host = "oss.sonatype.org",
          userName = System.getenv("maven_user"),
          passwd = System.getenv("maven_password")
        ),
        Credentials(
          realm = "Bintray API Realm",
          host = "api.bintray.com",
          userName = System.getenv("bintray_user"),
          passwd = System.getenv("bintray_password")
        )
      )
    }
  }

  def publishToMaven: Boolean = sys.env.get("MAVEN_PUBLISH").exists("true" ==)

  lazy val bintraySettings: Seq[Def.Setting[_]] = Seq(
    publishMavenStyle := true,
    bintrayOrganization := Some("outworkers"),
    bintrayRepository <<= scalaVersion.apply {
      v => if (v.trim.endsWith("SNAPSHOT")) "oss-snapshots" else "oss-releases"
    },
    bintrayReleaseOnPublish in ThisBuild := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true},
    licenses += ("Apache-2.0", url("https://github.com/outworkers/outworkers/blob/develop/LICENSE.txt"))
  ) ++ defaultPublishingSettings

  lazy val pgpPass: Option[Array[Char]] = Properties.envOrNone("pgp_passphrase").map(_.toCharArray)

  lazy val mavenSettings: Seq[Def.Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    pgpPassphrase in ThisBuild := {
      if (runningUnderCi && pgpPass.isDefined) {
        println("Running under CI and PGP password specified under settings.")
        println(s"Password longer than five characters: ${pgpPass.map(_.length > 5).getOrElse(false)}")
        pgpPass
      } else {
        println("Could not find settings for a PGP passphrase.")
        println(s"pgpPass defined in environemnt: ${pgpPass.isDefined}")
        println(s"Running under CI: $runningUnderCi")
        None
      }
    },
    publishTo <<= version.apply {
      v =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT")) {
          Some("snapshots" at nexus + "content/repositories/snapshots")
        } else {
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
        }
    },
    externalResolvers <<= resolvers map { rs =>
      Resolver.withDefaultResolvers(rs, mavenCentral = true)
    },
    licenses += ("Outworkers License", url("https://github.com/outworkers/morpheus/blob/develop/LICENSE.txt")),
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true },
    pomExtra :=
      <url>https://github.com/outworkers/morpheus</url>
        <scm>
          <url>git@github.com:outworkers/morpheus.git</url>
          <connection>scm:git:git@github.com:outworkers/morpheus.git</connection>
        </scm>
        <developers>
          <developer>
            <id>alexflav</id>
            <name>Flavian Alexandru</name>
            <url>http://github.com/alexflav23</url>
          </developer>
        </developers>
  ) ++ defaultPublishingSettings

  def effectiveSettings: Seq[Def.Setting[_]] = {
    if (sys.env.contains("MAVEN_PUBLISH")) mavenSettings else bintraySettings
  }

  def runningUnderCi: Boolean = sys.env.get("CI").isDefined || sys.env.get("TRAVIS").isDefined
  def travisScala211: Boolean = sys.env.get("TRAVIS_SCALA_VERSION").exists(_.contains("2.11"))

  def isTravisScala210: Boolean = !travisScala211

  def isJdk8: Boolean = sys.props("java.specification.version") == "1.8"

  lazy val addOnCondition: (Boolean, ProjectReference) => Seq[ProjectReference] = (bool, ref) =>
    if (bool) ref :: Nil else Nil

}