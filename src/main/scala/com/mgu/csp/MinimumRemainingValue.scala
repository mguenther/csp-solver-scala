package com.mgu.csp

/**
 * This `VariableOrdering` selects the unassigned [[Variable]] that has the fewest legal values
 * left. It is also known as the "most-constrained value" or "fail-first" heuristic, because it
 * select a variable that is most likely to cause a failure soon, thereby pruning the search tree.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
class MinimumRemainingValue[+A] extends VariableOrdering[A] {

  override def selectUnassignedVariable[B >: A](assignment: Assignment[B]): Option[Variable[B]] =
    assignment
      .unassignedVariables()
      .sortWith(mostConstrainedOf)
      .headOption

  private def mostConstrainedOf[B >: A](a: Variable[B], b: Variable[B]) =
    (a.domain.size - b.domain.size) < 0
}