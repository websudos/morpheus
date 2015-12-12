
import com.twitter.sbt.VersionManagement
import sbt.Keys._
import sbt._

object Build extends Build {

  val UtilVersion = "0.10.0"
  val FinagleVersion = "6.25.0"
  val SparkVersion = "1.2.1"
  val FinaglePostgres = "0.1.0-SNAPSHOT"
  val FinagleZkVersion = "6.28.0"
  val ShapelessVersion = "2.2.4"
  val DieselEngineVersion = "0.2.4"

  val bintrayPublishing: Seq[Def.Setting[_]] = Seq(
    publishMavenStyle := true,
    bintray.BintrayKeys.bintrayOrganization := Some("websudos"),
    bintray.BintrayKeys.bintrayRepository := "oss-releases",
    bintray.BintrayKeys.bintrayReleaseOnPublish := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true},
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))
  )

  val mavenPublishSettings : Seq[Def.Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    publishTo <<= version.apply {
      v =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true },
    pomExtra :=
      <url>https://github.com/websudos/morpheus</url>
        <licenses>
          <license>
            <name>Apache License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:websudos/morpheus.git</url>
          <connection>scm:git:git@github.com:websudos/morpheus.git</connection>
        </scm>
        <developers>
          <developer>
            <id>alexflav</id>
            <name>Flavian Alexandru</name>
            <url>http://github.com/alexflav23</url>
          </developer>
        </developers>
  )

  def liftVersion(scalaVersion: String) = {
    scalaVersion match {
      case "2.10.5" => "3.0-M1"
      case _ => "3.0-M2"
    }
  }

  val sharedSettings: Seq[Def.Setting[_]] = Seq(
    organization := "com.websudos",
    version := "0.2.3",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.10.5", "2.11.7"),
    resolvers ++= Seq(
      "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
      "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
      "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
      "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
      "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
      "Twitter Repository"               at "http://maven.twttr.com",
      Resolver.bintrayRepo("websudos", "oss-releases")
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
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++ bintrayPublishing ++ VersionManagement.newSettings

  lazy val morpheus = Project(
    id = "morpheus",
    base = file("."),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus"
  ).aggregate(
    morpheusDsl,
    morpheusMySQL,
    // morpheusPostgres,
    // morpheusSpark,
    morpheusTestkit
    // morpheusZookeeper
  )

  lazy val morpheusDsl = Project(
    id = "morpheus-dsl",
    base = file("morpheus-dsl"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-dsl",
    libraryDependencies ++= Seq(
      "org.slf4j"                    % "slf4j-api"                         % "1.7.12",
      "com.websudos"                 %% "diesel-engine"                    % DieselEngineVersion,
      "com.chuusai"                  %% "shapeless"                        % "2.2.4",
      "org.scala-lang"               % "scala-reflect"                     % scalaVersion.value,
      "joda-time"                    % "joda-time"                         % "2.3",
      "org.joda"                     % "joda-convert"                      % "1.6",
      "net.liftweb"                  %% "lift-json"                        % liftVersion(scalaVersion.value)                 % "test, provided"
    )
  ).dependsOn(
    morpheusTestkit % "test, provided"
  )

  lazy val morpheusMySQL = Project(
    id = "morpheus-mysql",
    base = file("morpheus-mysql"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-mysql",
    libraryDependencies ++= Seq(
      "com.twitter"                  %% "finagle-mysql"                     % FinagleVersion % "test, compile"
    )
  ).dependsOn(
    morpheusDsl,
    morpheusTestkit % "test, provided"
  )

  lazy val morpheusPostgres = Project(
    id = "morpheus-postgres",
    base = file("morpheus-postgres"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-postgres",
    libraryDependencies ++= Seq(
      // "com.twitter" %% "finagle-postgres" % "0.1.0-SNAPSHOT"
    )
  ).dependsOn(
    morpheusDsl,
    morpheusTestkit % "test, provided"
  )


/*
  lazy val morpheusZookeeper = Project(
    id = "morpheus-zookeeper",
    base = file("morpheus-zookeeper"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-zookeeper",
    libraryDependencies ++= Seq(
      "com.twitter"                  %% "finagle-zookeeper"                 % FinagleZkVersion,
      "com.websudos"                 %% "util-zookeeper"                    % UtilVersion
    )
  )

  lazy val morpheusSpark = Project(
    id = "morpheus-spark",
    base = file("morpheus-spark"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-spark",
    libraryDependencies ++= Seq(
      "org.apache.spark"             %% "spark-sql"                    % SparkVersion
    )
  ).dependsOn(
    morpheusTestkit
  )*/

  lazy val morpheusTestkit = Project(
    id = "morpheus-testkit",
    base = file("morpheus-testkit"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-testkit",
    libraryDependencies ++= Seq(
      "com.h2database"                   % "h2"                        % "1.4.181",
      "com.websudos"                     %% "util-testing"             % UtilVersion excludeAll(
        ExclusionRule("org.scala-lang", "scala-reflect")
      )
    )
  ).dependsOn(
    // morpheusZookeeper
  )
}
