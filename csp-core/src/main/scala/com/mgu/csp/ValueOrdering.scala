package com.mgu.csp

/**
 * A `ValueOrdering` implements a strategy to order the domain of any given [[Variable]].
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
trait ValueOrdering[+A] {
  /**
   * Orders the remaining set of domain values of the given [[Variable]] with regard to the
   * implemented strategy. Implementing classes can make use of the set of [[Constraint]]s
   * to make better decisions on how to order the domain.
   *
   * @param variable
   *    this is the variable whose domain shall be ordered with regard to the
   *    implemented strategy
   * @param constraints
   *    [[List]] of [[Constraint]]s of a CSP that this value ordering strategy can make use of
   *    to make informed decisions on how to order the domain of the given [[Variable]]
   * @return
   *    ordered [[List]] of [[Variable]]s
   */
  def orderedDomain[B >: A](variable: Variable[B], constraints: List[Constraint[B]]): List[B]
}

/**
 * This is the default implementation of [[ValueOrdering]]. It preserves the original order
 * of domain values from the given [[Variable]].
 */
class UninformedValueOrdering[+A] extends ValueOrdering[A] {
  override def orderedDomain[B >: A](variable: Variable[B], constraints: List[Constraint[B]]): List[B] =
    variable.domain
}