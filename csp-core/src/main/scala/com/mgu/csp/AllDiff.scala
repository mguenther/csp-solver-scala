package com.mgu.csp

import com.mgu.csp.Variable.Identity

import scala.annotation.tailrec

/**
 * The `AllDiff` constraint ensures that assigned variables the constraint relies on hold a unique value
 * with regard to each other. It also ensures that unassigned variables are not in a conflicting state. Suppose
 * variables X and Y are unassigned and share the same restricted domain of values D(X) = D(Y) = { c }. Both X
 * and Y are in a conflicted state and it is impossible to satisfy the constraint by further variable assignments.
 *
 * @param reliesOn
 *    represents the identities of all [[Variable]]s that this `Constraint` relies on
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
case class AllDiff[+A](reliesOn: List[Identity]) extends Constraint[A] {

  override def isConsistent[B >: A](dependentVariables: List[Variable[B]]): Boolean = {
    noDuplicatesAssigned(dependentVariables) &&
    unassignedCanBeAssigned(dependentVariables) &&
    noConflictingUnassigned(dependentVariables)
  }

  private def noDuplicatesAssigned[B >: A](dependentVariables: List[Variable[B]]): Boolean = {
    val assignedValues = dependentVariables
      .filter(variable => variable.isAssigned())
      .map(variable => variable.value)
    !containsDuplicates(assignedValues)
  }

  private def unassignedCanBeAssigned[B >: A](dependentVariables: List[Variable[B]]): Boolean = {
    val assignedValues = dependentVariables
      .filter(variable => variable.isAssigned())
      .map(variable => variable.value)
    dependentVariables
      .filterNot(variable => variable.isAssigned())
      .map(variable => variable.domain)
      .forall(candidates => candidates.exists(candidate => !assignedValues.contains(candidate)))
  }

  private def noConflictingUnassigned[B >: A](dependentVariables: List[Variable[B]]): Boolean = {
    val remainingValues = dependentVariables
      .filterNot(variable => variable.isAssigned())
      .collect { case v @ Variable(_) if v.domain.size == 1 => v.domain.head }
    !containsDuplicates(remainingValues)
  }

  @tailrec
  private def containsDuplicates[B](list: List[B], seen: Set[B] = Set[B]()): Boolean =
    list match {
      case x :: xs => if (seen.contains(x)) true else containsDuplicates(xs, seen + x)
      case _ => false
    }

  override def isSatisfied[B >: A](dependentVariables: List[Variable[B]]): Boolean = {
    lazy val allVariablesAssigned = dependentVariables.forall(variable => variable.isAssigned())
    allVariablesAssigned && isConsistent(dependentVariables)
  }
}