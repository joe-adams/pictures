name := "waldo"
scalaVersion := "2.11.8"
version := "1.0"
val akkaV = "2.4.14"
val akkaHttpV = "10.0.0"
libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "ch.qos.logback" % "logback-classic" % "1.1.7" withSources() withJavadoc(),
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0" withSources() withJavadoc(),
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4" withSources() withJavadoc(),
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5" withSources() withJavadoc(),
  "com.drewnoakes" % "metadata-extractor" % "2.9.1" withSources() withJavadoc(),
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0-M1" withSources() withJavadoc(),
  "org.postgresql" % "postgresql" % "9.4.1212" withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-actor" % akkaV withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-stream" % akkaV withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-testkit" % akkaV withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-http" % akkaHttpV withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "3.0.1" % "test" withSources() withJavadoc(),
  "org.postgresql" % "postgresql" % "9.4.1212.jre7"
)

flywayUrl := "jdbc:postgresql://localhost:5432/postgres"

flywayUser := "waldo"

flywayPassword := "waldo"

flywayLocations := Seq("filesystem:flyway")





