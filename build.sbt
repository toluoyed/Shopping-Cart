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
      "org.scalameta" %% "munit"       % "1.0.0" % Test
    )
  )
