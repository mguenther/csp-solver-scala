package com.mgu.csp

import com.mgu.csp.Variable.Identity

/**
 * Each variable X_i in a CSP has a non-empty domain D_i of possible values. Domain values are discrete and finite.
 * Variables can be part of a partial assignment.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
case class Variable[+A](id: Identity)(val value: Option[A] = None, val domain: List[A]) {
  /**
   * Removes the given value from the domain of possible values. This operation is only admissible if the
   * variable is still unassigned. This method will immediately return if the domain does not contain the
   * given value, since we are already in a converged state with regard to this operation.
   *
   * @param disallowedValue
   *    the value that ought to be removed from the set of possible domain values
   * @return
   *    copy of this `Variable` with an updated set of remaining domain values
   */
  def restrict[B >: A](disallowedValue: B): Variable[B] =
    copy()(value = value, domain = domain.filterNot(items => items == disallowedValue))

  /**
   * Assigns the given value to the variable and clears its list of domain values.
   *
   * @param value
   *    the value that is assigned to this { @code Variable}
   * @return
   *    copy of this `Variable` with an assigned value and cleared domain values
   */
  def assign[B >: A](value: B): Variable[B] =
    copy()(value = Some(value), domain = List.empty)

  def isAssigned(): Boolean =
    value.isDefined
}

object Variable {
  type Identity = String
}