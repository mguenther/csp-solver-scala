package com.mgu.csp.queens

import com.mgu.csp.DFSSolver
import com.mgu.csp.queens.IdGenerator.id

object QueensApp {

  def main(args: Array[String]): Unit = {
    val size = 8
    val queensCSP = new QueensCSP(size)
    val solver = new DFSSolver[Int]()
    val start = System.nanoTime()
    val assignment = solver.solve(queensCSP)
    val duration = (System.nanoTime() - start) / 1000000

    if (!assignment.isDefined) {
      println("Found no solution.")
    } else {
      println("Took " + duration + " ms.")
      println()

      (1 to size)
        .map(id(_))
        .foreach(row => {
        val queensPosition = assignment.get.variableAssignments.get(row).get.value.get
        print((1 to queensPosition-1).toList.foldLeft("")((s,_) => s + "."))
        print("Q")
        print((queensPosition+1 to size).toList.foldLeft("")((s,_) => s + "."))
        print(" [" + queensPosition + "]")
        println
      })
    }
  }
}