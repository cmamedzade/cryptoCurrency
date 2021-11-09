lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion    = "2.6.17"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.13.4"
    )),
    name := "jetsoft",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.2.3",
      "org.typelevel" %% "cats-core" % "2.3.0",
      "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.19.0-M14",
      "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % "0.19.0-M14",
      "com.softwaremill.sttp.tapir" %% "tapir-json-spray" % "0.19.0-M14"
    )
  )