package com.mgu.csp.queens

import com.mgu.csp.Variable._

object IdGenerator {
  def id(row: Int): Identity = {
    return String.format("R%s", String.valueOf(row))
  }
}