import sbt._
import Keys._

object ApplicationBuild extends Build {
  
  lazy val root = Project("scalaquery-magic", file("."))
      .aggregate(runtime, generator)
      .settings(commonSettings ++ Seq(
          publishArtifact := false): _*)

  lazy val runtime = Project("scalaquery-magic-runtime", file("runtime"))
      .settings(commonSettings ++ Seq(
          libraryDependencies += "org.scalaquery" % "scalaquery_2.9.0-1" % "0.9.5"): _*)

  lazy val generator = Project("scalaquery-magic-generator", file("generator"))
      .settings(commonSettings ++ Seq(
          resolvers += ("amateras snapshot" at "http://amateras.sourceforge.jp/mvn-snapshot/"),
          libraryDependencies += "jp.sf.amateras.scalagen" %% "scalagen-core" % "0.1-SNAPSHOT"): _*)

  def commonSettings = Defaults.defaultSettings ++
    Seq(
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.1",
      organization := "jp.sf.amateras.scalaquery-magic",
      publishTo <<= (version) { version: String =>
        val repoInfo =
          if (version.trim.endsWith("SNAPSHOT"))
            ("amateras snapshots" -> "/home/groups/a/am/amateras/htdocs/mvn-snapshot/")
          else
            ("amateras releases" -> "/home/groups/a/am/amateras/htdocs/mvn/")
            
        Some(Resolver.ssh(repoInfo._1, "shell.sourceforge.jp", repoInfo._2) as(
            System.getProperty("user.name"), (Path.userHome / ".ssh" / "id_rsa").asFile) withPermissions("0664"))
      },
      publishMavenStyle := true
    )          
}
