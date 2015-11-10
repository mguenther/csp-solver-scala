package com.mgu.csp

/**
 * A `VariableOrdering` implements a strategy to fetch the next unassigned [[Variable]].
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
trait VariableOrdering[+A] {
  /**
   * Selects the next unassigned variable of the given [[Assignment]] with regard to the
   * implemented strategy.
   *
   * @param assignment
   *    represents the current state of a CSP
   * @return
   *    unassigned variable
   */
  def selectUnassignedVariable[B >: A](assignment: Assignment[B]): Option[Variable[B]]
}

/**
 * This is the default implementation of [[VariableOrdering]]. It simply selects the
 * next unassigned variable with regard to the current state in [[Assignment]].
 */
class UninformedVariableOrdering[+A] extends VariableOrdering[A] {
  override def selectUnassignedVariable[B >: A](assignment: Assignment[B]): Option[Variable[B]] =
    assignment.unassignedVariables().headOption
}