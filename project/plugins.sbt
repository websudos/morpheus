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
resolvers ++= Seq(
  "jgit-repo" at "http://download.eclipse.org/jgit/maven",
  "Twitter Repo" at "http://maven.twttr.com/",
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayRepo("outworkers", "oss-releases"),
  Resolver.url("Outworkers OSS", url("http://dl.bintray.com/websudos/oss-releases"))(Resolver.ivyStylePatterns),
  Classpaths.sbtPluginReleases
)

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.1.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

addSbtPlugin("com.websudos" % "sbt-package-dist" % "1.2.0")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.22"
