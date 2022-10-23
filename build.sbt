name := "expressions-scala"

version := "0.4"

scalaVersion := "3.0.1"

scalacOptions ++= Seq("-deprecation", 
"-feature", 
"-unchecked", 
"-Yexplicit-nulls", 
"-language:strictEquality", 
"-language:higherKinds",
"-rewrite", 
"-new-syntax")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.0.0",
  "org.scalatest"          %% "scalatest"                % "3.2.9" % Test,
  "io.higherkindness"          %% "droste-core"               % "0.9.0-M3",
  "io.chrisdavenport"          %% "cats-scalacheck"           % "0.3.1" % Test,
  "org.typelevel"              %% "cats-laws"                 % "2.6.1" % Test
)

enablePlugins(JavaAppPackaging)

//scalacOptions ++= Seq("-rewrite", "-new-syntax")
//scalacOptions += "-Ypartial-unification"

// addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2")
// libraryDependencies += "org.typelevel" %% "cats-core" % "2.3.0"
// scalacOptions ++= Seq(
//   "-deprecation",
//   "-feature",
//   "-unchecked",
//   "-Yexplicit-nulls",
//   "-language:strictEquality",
//   "-language:higherKinds"
// )

// libraryDependencies ++= Seq(
//   "io.higherkindness"          %% "droste-core"               % "0.9.0-M3",
//   "io.chrisdavenport"          %% "cats-scalacheck"           % "0.3.1" % Test,
//   "org.typelevel"              %% "cats-laws"                 % "2.6.1" % Test
// )

//scalacOptions ++= Seq("-rewrite", "-new-syntax")
