package com.mgu.csp.sudoku

import com.mgu.csp.Variable.Identity

object IdGenerator {
  def id(row: Int, col: Int): Identity = {
    String.format("C%s%s", String.valueOf(row), String.valueOf(col))
  }
}