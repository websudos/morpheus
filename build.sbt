/*
 * Copyright 2013-2019 Outworkers, Limited.
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
import Publishing.{ciSkipSequence, releaseTutFolder}
import sbtrelease.ReleaseStateTransformations._

lazy val Versions = new {
  val util = "0.50.0"
  val spark = "1.2.1"
  val FinaglePostgres = "0.1.0"
  val shapeless = "2.3.2"
  val diesel = "0.5.0"
  val lift = "3.0"
  val slf4j = "1.7.21"
  val joda = "2.9.4"
  val scalatest = "3.0.5"
  val jodaConvert = "1.8.1"
  val scalacheck = "1.14.0"

  val finagle: String => String = { s =>
    CrossVersion.partialVersion(s) match {
      case Some((_, minor)) if minor >= 12 => "6.42.0"
      case _ => "6.35.0"
    }
  }

  val twitterUtil: String => String = {
    s => CrossVersion.partialVersion(s) match {
      case Some((_, minor)) if minor >= 12 => "6.41.0"
      case _ => "6.34.0"
    }
  }
}

val liftVersion: String => String = {
  s => CrossVersion.partialVersion(s) match {
    case Some((_, minor)) if minor >= 11 => Versions.lift
    case _ => "3.0-M1"
  }
}

lazy val releaseSettings = Seq(
  releaseTutFolder := baseDirectory.value / "docs",
  releaseIgnoreUntrackedFiles := true,
  releaseVersionBump := sbtrelease.Version.Bump.Minor,
  releaseTagComment := s"Releasing ${(version in ThisBuild).value} $ciSkipSequence",
  releaseCommitMessage := s"Setting version to ${(version in ThisBuild).value} $ciSkipSequence",
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    setReleaseVersion,
    Publishing.commitTutFilesAndVersion,
    releaseStepCommandAndRemaining("such publishSigned"),
    releaseStepCommandAndRemaining("sonatypeReleaseAll"),
    tagRelease,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

lazy val sharedSettings: Seq[Def.Setting[_]] = Seq(
  organization := "com.outworkers",
  scalaVersion := "2.11.12",
  crossScalaVersions := Seq("2.10.6", "2.11.12"),
  resolvers ++= Seq(
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    Resolver.jcenterRepo,
    "Twitter Repository" at "http://maven.twttr.com"
  ),
  scalacOptions ++= Seq(
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:higherKinds",
    "-language:existentials",
    "-Yinline-warnings",
    "-Xlint",
    "-deprecation",
    "-feature",
    "-unchecked"
  ),
  fork in Test := true,
  javaOptions in Test ++= Seq("-Xmx2G")
) ++ Publishing.effectiveSettings ++ releaseSettings

lazy val morpheus = (project in file("."))
  .settings(sharedSettings: _*)
  .settings(
    name := "morpheus",
    moduleName := "morpheus"
  ).aggregate(
    morpheusDsl,
    morpheusMySQL
  )

  lazy val morpheusDsl = (project in file("morpheus-dsl"))
    .settings(sharedSettings: _*)
    .settings(
      name := "morpheus-dsl",
      moduleName := "morpheus-dsl",
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
        "com.twitter" %% "util-core" % Versions.twitterUtil(scalaVersion.value),
        "org.slf4j" % "slf4j-api" % Versions.slf4j,
        "com.chuusai" %% "shapeless" % Versions.shapeless,
        "joda-time" % "joda-time" % Versions.joda,
        "org.joda" % "joda-convert" % Versions.jodaConvert,
        "net.liftweb" %% "lift-json" % liftVersion(scalaVersion.value) % Test,
        "com.outworkers"               %% "util-samplers"                     % Versions.util % Test,
        "org.scalatest"                %% "scalatest"                         % Versions.scalatest % Test,
        "org.scalacheck"               %% "scalacheck"                        % Versions.scalacheck % Test
      )
    )

  lazy val morpheusMySQL = (project in file("morpheus-mysql"))
    .settings(sharedSettings: _*)
    .settings(
      moduleName := "morpheus-mysql",
      name := "morpheus-mysql",
      libraryDependencies ++= Seq(
        "com.twitter"                  %% "finagle-mysql"                     % Versions.finagle(scalaVersion.value),
        "com.outworkers"               %% "util-samplers"                     % Versions.util % Test,
        "org.scalatest"                %% "scalatest"                         % Versions.scalatest % Test,
        "org.scalacheck"               %% "scalacheck"                        % Versions.scalacheck % Test
      )
    ).dependsOn(
      morpheusDsl % "compile->compile;test->test;"
    )
