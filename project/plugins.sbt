resolvers ++= Seq(
  "Sonatype snapshots"                                 at "http://oss.sonatype.org/content/repositories/snapshots/",
  "jgit-repo"                                          at "http://download.eclipse.org/jgit/maven",
  "Twitter Repo"                                       at "http://maven.twttr.com/",
  "sonatype-releases"                                  at "https://oss.sonatype.org/content/repositories/releases/",
  Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns),
  Resolver.bintrayRepo("websudos", "oss-releases"),
  Classpaths.sbtPluginReleases
)

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")

// addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "3.18.1")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.1")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.0.0")

// addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

addSbtPlugin("com.websudos" % "sbt-package-dist" % "1.2.0")

