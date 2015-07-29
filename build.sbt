name := """words"""

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation",
  "-Xlint", "-Ybackend:GenBCode", "-encoding", "UTF8",
  "-Ywarn-unused-import","-Xfatal-warnings")


// Change this to another test framework if you prefer
libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe.akka" % "akka-stream-experimental_2.11" % "1.0",
  "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "1.0",
  "com.typesafe.akka" % "akka-http-experimental_2.11" % "1.0",
  "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "1.0")

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"


scalastyleConfig := baseDirectory.value / "src/test/resources/scalastyle-config.xml"

// Create a default Scala style task to run with tests
lazy val testScalastyle = taskKey[Unit]("testScalastyle")

testScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Test).toTask("").value

(test in Test) <<= (test in Test) dependsOn testScalastyle