package com.mgu.csp

/**
 * This is the abstract representation of a CSP. A CSP is defined by a set of variables X_i (through
 * its initial assignment) and a set of constraints. It provides boolean accessors to determine whether
 * a given assignment is consistent or satisfied with its definition. A solution to the CSP is an
 * assignment that is both complete and satisfies all constraints.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
trait CSP[+A] {
  /**
   * Determines whether the given [[Assignment]] is consistent with regard to the [[Constraint]]s
   * of this CSP.
   *
   * @param assignment
   *    an [[Assignment]] represents the current state of this CSP
   * @return
   *    `true` if the given [[Assignment]] is consistent, `false` otherwise
   */
  final def isConsistent[B >: A](assignment: Assignment[B]): Boolean = {
    assignment.isConsistent(constraints())
  }

  /**
   * Determines whether the given [[Assignment]] is satisfied with regard to the [[Constraint]]s
   * of this CSP.
   *
   * @param assignment
   *    an [[Assignment]] represents the current state of this CSP
   * @return
   *    `true` if the given [[Assignment]] is satisfied, `false` otherwise
   */
  final def isSatisfied[B >: A](assignment: Assignment[B]): Boolean =
    assignment.isSatisfied(constraints())

  /**
   * @return
   *    Yields the set of constraints for this CSP
   */
  def constraints(): List[Constraint[A]]

  /**
   * @return
   *    Yields the initial assignment for this CSP
   */
  def initialAssignment(): Assignment[A]
}