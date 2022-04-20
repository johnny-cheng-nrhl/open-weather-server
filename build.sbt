val Http4sVersion = "0.21.4"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val CirceVersion = "0.13.0"
val ScalaTestVersion = "3.2.3"
val CatsEffectVersion = "2.1.3"
val ScalaCheckVersion = "1.15.4"
val ScalaTestPlusVersion = "3.2.2.0"

lazy val root = (project in file("."))
  .settings(
    organization := "org.hautelook",
    name := "http4squickstart",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.typelevel"   %% "cats-effect"         % CatsEffectVersion,
      "org.typelevel"   %% "cats-effect-laws"    % CatsEffectVersion % Test,
      "org.scalatest"   %% "scalatest"           % ScalaTestVersion  % "test",
      "org.scalacheck"  %% "scalacheck"          % ScalaCheckVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-14"   % ScalaTestPlusVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
    ),
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
//    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )