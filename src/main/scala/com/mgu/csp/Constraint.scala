package com.mgu.csp

import com.mgu.csp.Variable.Identity

/**
 * A `Constraint` involves some subset of the variables of a CSP and specifies the allowable
 * combinations of values for that subset.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
trait Constraint {
  /**
   * Determines whether this `Constraint` is consistent with the current state of its dependent
   * variables.
   *
   * @param dependentVariables
   *    Subset of variables of the CSP that this `Constraint` relies on. The variables within
   *    this subset represent a subset of the current state (cf. [[Assignment]]) of the CSP.
   * @return
   *    `true` if this `Constraint` is consistent with the given [[Variable]]s, `false` otherwise
   */
  def isConsistent[A](dependentVariables: List[Variable[A]]): Boolean

  /**
   * Determines whether this `Constraint` is satisfied with the current state of its dependent
   * variables.
   *
   * @param dependentVariables
   *    Subset of variables of the CSP that this `Constraint` relies on. The variables within
   *    this subset represent a subset of the current state (cf. [[Assignment]]) of the CSP.
   * @return
   *    `true` if this `Constraint` is satisfied with the given [[Variable]]s, `false` otherwise
   */
  def isSatisfied[A](dependentVariables: List[Variable[A]]): Boolean

  /**
   * @return
   *    immutable [[List]] of [[Identity]] that identifies the set of variables this
   *    particular `Constraint` relies on
   */
  def reliesOn(): List[Identity]
}