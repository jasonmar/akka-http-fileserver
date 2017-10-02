name := "akka-http-fileserver"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.10"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

mainClass in (Compile, run) := Some("fileserver")
