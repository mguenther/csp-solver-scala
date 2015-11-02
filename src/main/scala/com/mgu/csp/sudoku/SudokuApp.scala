package com.mgu.csp.sudoku

import com.mgu.csp.{MinimumRemainingValue, DFSSolver}
import com.mgu.csp.sudoku.IdGenerator.id

object SudokuApp {

  val SUDOKU_PUZZLE: String =
      "003020600\n" +
      "900305001\n" +
      "001806400\n" +
      "008102900\n" +
      "700000008\n" +
      "006708200\n" +
      "002609500\n" +
      "800203009\n" +
      "005010300"

  def main(args: Array[String]): Unit = {
    val sudokuCSP = new SudokuCSP(SUDOKU_PUZZLE)
    val solver = new DFSSolver[Int](variableOrdering = new MinimumRemainingValue[Int])
    val start = System.nanoTime()
    val assignment = solver.solve(sudokuCSP)
    val duration = (System.nanoTime() - start) / 1000000

    if (!assignment.isDefined) {
      println("Found no solution.")
    } else {
      println("Took " + duration + " ms.")
      val rows = for {
        row <- 1 to 9
      } yield (1 to 9).map(id(row, _))

      rows.foreach(row => {
        println()
        row.foreach(col => print(assignment.get.variableAssignments.get(col).get.value))
      })
    }
  }
}