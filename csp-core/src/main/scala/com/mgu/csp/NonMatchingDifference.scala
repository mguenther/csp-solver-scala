package com.mgu.csp

import com.mgu.csp.Variable.Identity

/**
 * The `NonMatchingDifference` constraint computes the difference over a list of dependent
 * variables, where the head value of that list is the basis from which each value of the tail
 * gets substracted. It then compares if the resulting difference is not equal to a given
 * difference.
 * 
 * Please note that this constraint is only able to obtain a result if all dependent variables
 * are assigned. Thus, it checks for that condition and shortcircuits any state in which there
 * is at least one unassigned value by yielding `true`, so that a solver algorithm can progress.
 * 
 * @param reliesOn
 *    represents the identities of all [[Variable]]s that this `Constraint` relies on
 * @param differenceMustNotMatch
 *    represents the non-matching difference
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
case class NonMatchingDifference(reliesOn: List[Identity], differenceMustNotMatch: Int) extends Constraint[Int] {

  override def isConsistent[B >: Int](dependentVariables: List[Variable[B]]): Boolean =
    anyUnassigned(dependentVariables) || nonMatchingDifference(dependentVariables)

  private def anyUnassigned[B >: Int](dependentVariables: List[Variable[B]]): Boolean =
    dependentVariables.exists(variable => !variable.isAssigned())

  private def nonMatchingDifference[B >: Int](dependentVariables: List[Variable[B]]): Boolean = {
    val values = dependentVariables.map(variable => variable.value)
    val difference = values
      .tail
      .foldLeft[Int](values.head.get.asInstanceOf[Int])((partialDifference, b) => partialDifference - b.get.asInstanceOf[Int])
    difference != differenceMustNotMatch
  }

  override def isSatisfied[B >: Int](dependentVariables: List[Variable[B]]): Boolean = {
    lazy val allVariablesAssigned = dependentVariables.forall(variable => variable.isAssigned())
    allVariablesAssigned && isConsistent(dependentVariables)
  }
}