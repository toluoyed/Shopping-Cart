ThisBuild / scalaVersion := "3.3.3"
ThisBuild / organization := "com.shoppingcart"

val CatsVersion       = "2.10.0"
val CatsEffectVersion = "3.5.4"
val Http4sVersion = "0.23.30"

lazy val root = (project in file("."))
  .settings(
    name := "shopping-cart",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"   % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,
      "org.typelevel" %% "squants" % "1.8.3",
      "dev.profunktor" %% "redis4cats-effects" % "2.0.3",
      "eu.timepit"    %% "refined"       % "0.11.2",
      "io.monix"      %% "newtypes-core" % "0.3.0",
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.scalameta" %% "munit"         % "1.0.0" % Test
    )
  )
