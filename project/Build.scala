/*
 * Copyright 2013 mengke@me.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import sbt._
import Keys._
import scala.Some

object Build extends Build {

  import Dependencies._

  lazy val root = Project("Titan",file("."))
    .aggregate(titanCore, titanCoreTests)
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)


  lazy val titanCore = Project("titan-core", file("titan-core"))
    .settings(titanModuleSettings: _*)
    .settings(libraryDependencies ++=
      compile(netty) ++
      compile(slf4j) ++
      compile(akkaActor) ++
      runtime(logback)
    )

  lazy val titanCoreTests = Project("titan-core-tests", file("titan-core-tests"))
    .dependsOn(titanCore)
    .settings(titanModuleSettings: _*)
    .settings(noPublishing: _*)
    .settings(libraryDependencies ++=
      test(logback) ++
      test(scalatest) ++
      test(scalacheck)
    )

  lazy val basicSettings = Seq(
    version               := "0.1.0",
    organization          := "io.github.mengke",
    organizationHomepage  := Some(new URL("http://mengke.github.io")),
    description           := "Titan aims to be a Jvm-based platform for horizontally partitioning for Mysql and build upon scala.",
    startYear             := Some(2013),
    licenses              := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion          := "2.11.1",
    resolvers             ++= Dependencies.resolutionRepos,
    scalacOptions         := Seq(
      "-encoding", "utf8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-Xlog-reflective-calls"
    )
  )

  lazy val noPublishing = Seq(
    publish := (),
    publishLocal := ()
  )

  lazy val titanModuleSettings =
    basicSettings

}

object Dependencies {

  object Versions {
    val scalaTestVersion = System.getProperty("akka.build.scalaTestVersion", "2.2.0")
    val scalaCheckVersion = System.getProperty("akka.build.scalaCheckVersion", "1.11.4")
  }

  val resolutionRepos = Seq(
    "spray repo" at "http://repo.spray.io/",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  import Versions._

  val slf4j         = "org.slf4j"                               %   "slf4j-api"                   % "1.7.7"         // MIT
  val logback       = "ch.qos.logback"                          %   "logback-classic"             % "1.1.2" excludeAll (
    ExclusionRule(organization = "org.slf4j")
  )                                                                                                                // EPL 1.0 / LGPL 2.1
  val netty         = "io.netty"                                %   "netty-all"                   % "5.0.0.Alpha1"  // ApacheV2
  val akkaActor     = "com.typesafe.akka"                       %%  "akka-actor"                  % "2.3.3"        // ApacheV2

  val scalatest    = "org.scalatest"                            %%  "scalatest"                   % scalaTestVersion  // ApacheV2
  val scalacheck   = "org.scalacheck"                           %%  "scalacheck"                  % scalaCheckVersion // New BSD

}