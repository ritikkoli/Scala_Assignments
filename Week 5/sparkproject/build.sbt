

//ThisBuild / version := "0.1.0-SNAPSHOT"
//
//ThisBuild / scalaVersion := "2.12.10"
//
//lazy val root = (project in file("."))
//  .settings(
//    name := "scala-spark"
//  )
//
//val sparkVersion = "3.2.1"
//
//libraryDependencies ++= Seq(
//  "org.apache.spark" %% "spark-core" % sparkVersion,
//  "org.apache.spark" %% "spark-sql" % sparkVersion,
//
//  "org.apache.hadoop" % "hadoop-common" % "3.2.0",
//  "org.apache.hadoop" % "hadoop-aws" % "3.2.0",
//
//  "com.amazonaws" % "aws-java-sdk-bundle" % "1.11.375",
//  "mysql" % "mysql-connector-java" % "8.0.19"
//)
//
//dependencyOverrides ++= Seq(
//  "org.apache.hadoop" % "hadoop-common" % "3.2.0",
//  "org.apache.hadoop" % "hadoop-hdfs" % "3.2.0",
//  "org.apache.hadoop" % "hadoop-hdfs-client" % "3.2.0",
//  "org.apache.hadoop" % "hadoop-client" % "3.2.0",
//  "org.apache.hadoop" % "hadoop-client-runtime" % "3.2.0",
//  "org.apache.hadoop" % "hadoop-client-api" % "3.2.0"
//)



ThisBuild / scalaVersion := "2.12.15"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.payoda"


val sparkVersion = "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "urbanmove-spark",

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion,
      "org.apache.spark" %% "spark-streaming" % sparkVersion,
      "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
      "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
      "mysql" % "mysql-connector-java" % "8.0.33",
      "org.scalatest" %% "scalatest" % "3.2.2" % Test
    )



)