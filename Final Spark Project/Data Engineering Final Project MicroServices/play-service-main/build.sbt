name := """scala-play"""
organization := "com.scalaplay"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"


// -----------------------------
// PLAY + TEST DEPENDENCIES
// -----------------------------
libraryDependencies += guice

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test


// -----------------------------
// DATABASE (Slick + Evolutions) + MySQL
// -----------------------------
libraryDependencies ++= Seq(
  "org.playframework" %% "play-slick"            % "6.1.0",
  "org.playframework" %% "play-slick-evolutions" % "6.1.0",

  // Updated MySQL connector
  "com.mysql" % "mysql-connector-j" % "8.3.0"
)


// -----------------------------
// AKKA / STREAMS
// -----------------------------
// Keep only Akka OR Pekko. I kept your Akka set since Play is Akka-based internally.
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",
  "com.typesafe.akka" %% "akka-actor"  % "2.6.20",
  "com.typesafe.akka" %% "akka-slf4j"  % "2.6.20"
)


// -----------------------------
// JWT
// -----------------------------
libraryDependencies += "com.auth0" % "java-jwt" % "4.3.0"


// -----------------------------
// JSON (Correct dependency!)
// -----------------------------
libraryDependencies += "org.playframework" %% "play-json" % "3.0.3"


// -----------------------------
// Play Filters
// -----------------------------
libraryDependencies += filters


// -----------------------------
// Twirl & Routes (optional)
// -----------------------------
// TwirlKeys.templateImports += "com.scalaplay.controllers._"
// play.sbt.routes.RoutesKeys.routesImport += "com.scalaplay.binders._"

libraryDependencies += "software.amazon.awssdk" % "s3" % "2.20.0"
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.3.1"

libraryDependencies +="com.datastax.oss" % "java-driver-core" % "4.15.0"

libraryDependencies += "org.apache.parquet" % "parquet-avro" % "1.12.3"
libraryDependencies += "org.apache.parquet" % "parquet-common" % "1.12.3"

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "s3" % "2.20.0",
  "org.apache.hadoop" % "hadoop-common" % "3.3.1",
  "org.apache.hadoop" % "hadoop-aws" % "3.3.1",
  "org.apache.parquet" % "parquet-avro" % "1.12.3",
  "org.apache.parquet" % "parquet-common" % "1.12.3"
)
libraryDependencies ++= Seq(
  // Hadoop core
  "org.apache.hadoop" % "hadoop-common" % "3.3.4",
  "org.apache.hadoop" % "hadoop-client" % "3.3.4",

  // AWS S3 filesystem support
  "org.apache.hadoop" % "hadoop-aws" % "3.3.4",
  "com.amazonaws" % "aws-java-sdk-bundle" % "1.12.767",

  // Parquet Avro reader
  "org.apache.parquet" % "parquet-avro" % "1.13.1",
  "org.apache.parquet" % "parquet-hadoop" % "1.13.1"
)
