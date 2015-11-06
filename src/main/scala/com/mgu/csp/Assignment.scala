package com.mgu.csp

import com.mgu.csp.Variable.Identity

/**
 * A state of the CSP is defined by an assignment of values to some or all of the
 * variables. An assignment that does not violate any constraints is called a consistent
 * or legal assignment. A complete assignment is one in which every variable is mentioned.
 * A solution to the CSP is a complete assignment which does not violate any constraints.
 *
 * This `Assignment` applies forward checking. Thus, whenever a [[Variable]] is
 * assigned to a value, that value is removed from the domain of all dependent unassigned
 * variables.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
case class Assignment[+A](variableAssignments: Map[Identity, Variable[A]]) {
  /**
   * An assignment that does not violate any constraint is called consistent.
   *
   * @param constraints
   *    [[List]] of [[Constraint]]s of a [[CSP]] that this particular assignment
   *    should be checked against for consistency
   * @return
   *    `true` if this assignment is consistent with regard to the given [[Constraint]]s,
   *    `false` otherwise
   */
  def isConsistent[B >: A](constraints: List[Constraint[B]]): Boolean =
    constraints.forall(constraint => constraint.isConsistent(subsetOf(constraint.reliesOn())))

  /**
   * A complete assignment is one in which every variable of the [[CSP]] is mentioned.
   *
   * @return
   *    `true` if this assignment is complete with regard to the given [[List]] of
   *    [[Variable]]s, `false` otherwise
   */
  def isComplete(): Boolean =
    variableAssignments.forall { case (id, variable) => variable.isAssigned() }

  /**
   * A solution to a [[CSP]] is a complete assignment that satisfies all [[Constraint]]s.
   *
   * @param constraints
   *    [[List]] of [[Constraint]]s of a [[CSP]] that this particular assignment
   *    should be checked against
   * @return
   *    `true` if this assignment is satisfied with regard to the given [[Constraint]]s,
   *    `false` otherwise
   */
  def isSatisfied[B >: A](constraints: List[Constraint[B]]): Boolean =
    isComplete && constraints.forall(constraint => constraint.isSatisfied(subsetOf(constraint.reliesOn())))

  private def subsetOf(variableIdentities: List[Identity]): List[Variable[A]] =
    variableIdentities.flatMap(variableIdentity => variableAssignments.get(variableIdentity))

  /**
   * Assigns the value of type `B >: A` to the given variable. The value must be in the domain of that
   * variable, otherwise the assignment will fail.
   *
   * @param variableIdentity
   *    identifies the variable that the given value will be assigned to
   * @param value
   *    this is the value that will be assigned to the variable
   * @return
   *    copy of this `Assignment` with the additional variable assignment based on the given parameters
   */
  def assign[B >: A](variableIdentity: Identity, value: B, constraints: List[Constraint[B]]): Assignment[B] = {
    val modifiedVariable = variableAssignments
      .get(variableIdentity)
      .map(variable => variable.assign(value))
      .get
    val baseAssignment = copy(variableAssignments = variableAssignments + ((variableIdentity, modifiedVariable)))
    dependentVariables(variableIdentity, constraints)
      .foldLeft(baseAssignment)((assignment, dependentVariableIdentity) => assignment.restrict(dependentVariableIdentity, value))
  }

  private def dependentVariables[B >: A](variableIdentity: Identity, constraints: List[Constraint[B]]): List[Identity] =
    constraints
      .filter(constraint => constraint.reliesOn().contains(variableIdentity))
      .flatMap(dependentConstraint => dependentConstraint.reliesOn())
      .filterNot(identity => identity == variableIdentity)
      .filterNot(identity => variableAssignments.get(identity).exists(variable => variable.isAssigned()))

  /**
   * Restricts the domain of the given variable by removing the given value from its domain.
   *
   * Please note that any variable that has a restricted set of possible domain values and not a fixed assignment
   * is an unassigned variable.
   *
   * @param variableIdentity
   *    uniquely identifies a variable within this `Assignment`
   * @param value
   *    the value that ought to be removed from the domain of the variable identified by the given identity
   * @return
   *    copy of this `Assignment` with the updated domain for the referenced variable
   */
  def restrict[B >: A](variableIdentity: Identity, value: B): Assignment[B] = {
    val restrictedVariable = variableAssignments
      .get(variableIdentity)
      .map(variable => variable.restrict(value))
      .get
    copy(variableAssignments = variableAssignments + ((variableIdentity, restrictedVariable)))
  }

  /**
   * @return
   *    immutable [[List]] of all [[Variable]]s that are not assigned.
   */
  def unassignedVariables(): List[Variable[A]] =
    variableAssignments
      .values
      .filterNot(variable => variable.isAssigned())
      .toList
}