ThisBuild / scalaVersion := "3.3.3"
ThisBuild / organization := "com.shoppingcart"

val CatsVersion       = "2.10.0"
val CatsEffectVersion = "3.5.4"

lazy val root = (project in file("."))
  .settings(
    name := "shopping-cart",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"   % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,
      "dev.profunktor" %% "redis4cats-effects" % "2.0.3",
      "eu.timepit"    %% "refined"       % "0.11.2",
      "io.monix"      %% "newtypes-core" % "0.3.0",
      "org.scalameta" %% "munit"         % "1.0.0" % Test
    )
  )
