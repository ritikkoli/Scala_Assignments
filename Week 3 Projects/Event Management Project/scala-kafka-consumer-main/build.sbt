ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "kafka-consumer-event-management"
  )

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

lazy val akkaVersion = sys.props.getOrElse("akka.version", "2.8.8")

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.13",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",
  "com.typesafe.akka" %% "akka-http" % "10.5.1",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
  "org.apache.kafka" %% "kafka" % "3.7.0" ,// Kafka client,
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.20"
)

libraryDependencies ++= Seq(
  "com.sun.mail" % "javax.mail" % "1.6.2",
  "javax.activation" % "activation" % "1.1.1",
// Play + Slick
"org.playframework" %% "play-slick" % "6.1.0",
"org.playframework" %% "play-slick-evolutions" % "6.1.0",
)

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.5.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
  "mysql"               %  "mysql-connector-java" % "8.0.26"
)
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.4.14",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
)


