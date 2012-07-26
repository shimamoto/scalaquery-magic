import sbt._
import Keys._

object ApplicationBuild extends Build {
  lazy val root = Project("scalaquery-magic", file("."))
      .aggregate(runtime, generator)
      .settings(Seq(
          version := "0.1",
          scalaVersion := "2.9.1"): _*)

  lazy val runtime = Project("scalaquery-magic-runtime", file("runtime"))
      .settings(Seq(
          libraryDependencies += "org.scalaquery" % "scalaquery_2.9.0-1" % "0.9.5"): _*)

  lazy val generator = Project("scalaquery-magic-generator", file("generator"))
      .settings(Seq(
          resolvers += ("amateras snapshot" at "http://amateras.sourceforge.jp/mvn-snapshot/"),
          libraryDependencies += "jp.sf.amateras.scalagen" %% "scalagen-core" % "0.1-SNAPSHOT"): _*)

}
