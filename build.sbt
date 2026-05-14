ThisBuild / scalaVersion := "3.3.3"
ThisBuild / organization := "com.shoppingcart"

lazy val root = (project in file("."))
  .settings(
    name := "shopping-cart",
    version := "0.1.0-SNAPSHOT"
  )
