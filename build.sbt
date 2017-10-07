name := """quizCapture"""
organization := "com.companyname"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.194"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"
libraryDependencies += javaJdbc % Test
libraryDependencies += "org.mockito" % "mockito-core" % "1.10.19"

scalaVersion := "2.12.2"
fork in run := false

