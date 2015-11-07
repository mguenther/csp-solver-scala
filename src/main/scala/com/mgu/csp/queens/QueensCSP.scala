package com.mgu.csp.queens

import com.mgu.csp.Variable.Identity
import com.mgu.csp.{ AllDiff, Assignment, Constraint, CSP, NonMatchingDifference, Variable }
import com.mgu.csp.queens.IdGenerator.id

class QueensCSP(size: Int = 8) extends CSP[Int] {

  override def constraints(): List[Constraint[Int]] =
    columnConstraints union diagonalConstraints((rowX, rowY) => rowX-rowY) union diagonalConstraints((rowX, rowY) => rowY-rowX)

  private def columnConstraints(): List[Constraint[Int]] =
    (for {
      rowX <- 1 to size
      rowY <- 1 to size
    } yield(rowX, rowY))
      .filter { case (rowX, rowY) => rowX != rowY }
      .map { case (rowX, rowY) => AllDiff[Int](List(id(rowX), id(rowY))) }
      .toList

  private def diagonalConstraints(f: (Int, Int) => Int): List[Constraint[Int]] =
    (for {
      rowX <- 1 to size
      rowY <- 1 to size
    } yield (rowX, rowY))
      .filter { case (rowX, rowY) => rowX != rowY }
      .map { case (rowX, rowY) => NonMatchingDifference(List(id(rowX), id(rowY)), f(rowX, rowY)) }
      .toList

  override def initialAssignment(): Assignment[Int] = {
    val unassignedVariables: Map[Identity, Variable[Int]] = (for {
      row <- 1 to size
    } yield id(row))
    .map(identity => identity -> Variable[Int](identity)(None, domain = (1 to size).toList))
    .toMap
    Assignment[Int](unassignedVariables)
  }
}