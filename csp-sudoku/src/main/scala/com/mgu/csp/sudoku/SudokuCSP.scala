package com.mgu.csp.sudoku

import com.mgu.csp.Variable.Identity
import com.mgu.csp.sudoku.IdGenerator.id
import com.mgu.csp.{AllDiff, Assignment, CSP, Constraint, Variable}

class SudokuCSP(val sudokuPuzzle: String) extends CSP[Int] {

  override def constraints(): List[Constraint[Int]] =
    constraintsOnRows union constraintsOnColumns union constraintsOnGrids

  private def constraintsOnRows(): List[Constraint[Int]] = {
    (for { row <- 1 to 9 } yield (1 to 9).map(col => id(row, col)))
      .map(dependentVariables => AllDiff[Int](dependentVariables.toList))
      .toList
  }

  private def constraintsOnColumns(): List[Constraint[Int]] = {
    (for { column <- 1 to 9 } yield (1 to 9).map(row => id(row, column)))
    .map(dependentVariables => AllDiff[Int](dependentVariables.toList))
    .toList
  }

  private def constraintsOnGrids(): List[Constraint[Int]] = {
    (for {
      gridRow <- 0 to 2
      gridCol <- 0 to 2
    } yield for {
        row <- 1 + gridRow * 3 to 1 + gridRow * 3 + 2
        col <- 1 + gridCol * 3 to 1 + gridCol * 3 + 2
      }  yield id(row, col))
    .map(dependentVariables => AllDiff[Int](dependentVariables.toList))
    .toList
  }

  override def initialAssignment(): Assignment[Int] = {

    val unassignedVariables: Map[Identity, Variable[Int]] = (for {
      row <- 1 to 9
      col <- 1 to 9
    } yield (row, col))
    .map { case (row, col) => id(row, col) }
    .map(identity => identity -> Variable[Int](identity)(None, domain = (1 to 9).toList))
    .toMap

    lazy val lazyConstraints: List[Constraint[Int]] = constraints()
    val emptyAssignment = Assignment[Int](unassignedVariables)
    val lines = sudokuPuzzle.split("\n")

    (for {
      line <- 0 to 8
      charAt <- 0 to 8
    } yield (line, charAt))
    .map { case (line, charAt) => (id(line+1, charAt+1), lines(line).charAt(charAt).asDigit) }
    .filterNot { case (id, number) => number == 0 }
    .foldLeft(emptyAssignment)((assignment, idAndValue) => assignment.assign(idAndValue._1, idAndValue._2, lazyConstraints))
  }
}