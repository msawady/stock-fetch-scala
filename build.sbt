name := "stock-fetch-scala"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.7" % Test,
  "com.typesafe.akka" %% "akka-actor" % "2.5.21",
  "com.typesafe.akka" %% "akka-stream" % "2.5.21",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.21" % Test,

  "com.typesafe.play" %% "play-json" % "2.7.2",
  "com.github.slack-scala-client" %% "slack-scala-client" % "0.2.6",
  "com.twitter" %% "util-core" % "19.10.0",

  "com.lihaoyi" %% "pprint" % "0.5.3"
)

