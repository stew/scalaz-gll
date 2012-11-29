package scalaz.gll

import sbt._
import sbt.Defaults._
import Keys._

object ScalazGllBuild extends Build {
  val parsnip_dependencies = Seq(
    "org.scalaz" %% "scalaz-core" % "7.0.0-M4",
    "com.codecommit" %% "gll-combinators" % "2.2-SNAPSHOT",
    "org.specs2" %% "specs2" % "1.9"
    )

  val scalazGllSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.vireo",
    name         := "scalaz-gll",
    version      := "0.1",
    scalaVersion := "2.9.2"
  )
 
  lazy val project = Project("scalaz-gll", file("."), settings = scalazGllSettings    
                             ++ Seq(resolvers ++= Seq(),
		             libraryDependencies ++= parsnip_dependencies
                                    )
                           )
}


