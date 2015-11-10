lazy val commonSettings = Seq(
  organization := "com.mgu",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val csp = (project in file("csp-core"))
  .settings(commonSettings: _*)

lazy val sudoku = (project in file("csp-sudoku"))
  .settings(commonSettings: _*)
  .dependsOn(csp)

lazy val queens = (project in file("csp-queens"))
  .settings(commonSettings: _*)
  .dependsOn(csp)