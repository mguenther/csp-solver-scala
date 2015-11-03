package com.mgu.csp

/**
 * Performs a backtracking kind of search by progressing along the state space in a depth-first manner.
 * The solver can make use of user-supplied heuristics for picking the next unassigned variable
 * (cf. [[VariableOrdering]]) and ordering the set of domain values for such an unassigned variable
 * (cf. [[ValueOrdering]]).
 *
 * By default, this backtracking uses uninformed heuristics for [[VariableOrdering]] and
 * [[ValueOrdering]].
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
class DFSSolver[+A](
     val variableOrdering: VariableOrdering[A] = new UninformedVariableOrdering[A](),
     val valueOrdering: ValueOrdering[A] = new UninformedValueOrdering[A]()) {
  /**
   * Solves the given CSP by performing a depth-first search starting off from the initial state (the
   * initial assignment).
   *
   * @param csp
   *    represents the CSP to solve
   * @return
   *    `Some` [[Assignment]] that is completed, or `None` if no such [[Assignment]] can be found
   */
  final def solve[B >: A](csp: CSP[B]): Option[Assignment[B]] = solve(csp, csp.initialAssignment())

  private def solve[B >: A](csp: CSP[B], assignment: Assignment[B]): Option[Assignment[B]] =
    csp.isSatisfied(assignment) match {
      case true => Some(assignment)
      case _ => {
        val unassignedVariable = variableOrdering.selectUnassignedVariable(assignment)
        lazy val constraints = csp.constraints()

        valueOrdering
          .orderedDomain(unassignedVariable, constraints)
          .toStream // crucial, otherwise this will be solved using a BFS which is painfully inefficient
          .map(value => assignment.assign(unassignedVariable.id, value, constraints))
          .filter(assignment => csp.isConsistent(assignment))
          .map(consistentAssignment => solve(csp, consistentAssignment))
          .flatten
          .find(_ => true)
      }
    }
}