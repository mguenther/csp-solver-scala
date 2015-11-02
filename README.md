# Constraint Satisfaction Problem Solver

This is a Scala-based port of the CSP solver `csp-solver-java`. It is a tiny framework for solving constraint
satisfaction problems (CSP) that have both discrete and finite domains. Although this framework is fully
functional, it is not meant to be production-ready, as it is primarily a small exercise for myself on
how to do functional programming with Scala.

The implementation is largely inspired from the splendid introductory text "Artificial Intelligence - A Modern Approach"
by Stuart Russell and Peter Norvig.

# Package Overview

* `com.mgu.csp`: Contains a functional approach on CSP-framework design for CSPs that are both discrete and finite.
* `com.mgu.csp.sudoku`: Contains an example application which expresses Sudoku as a CSP and solves it using the core framework.

# Design

The framework builds upon a set of a few abstractions. Trait `CSP` is the basis for all domain-specific
CSPs. It provides the means to construct the initial assignment, which is comprised of all the `Variable`s of the CSP
and their initial state. A variable can be either *assigned* or *unassigned*. In the first case, its domain has been
reduced to a single fixed value, while in the latter case it has no assigned value, but a - possibly reduced - set
of admissible domain values. Trait `CSP` also provides the means to construct the full set of `Constraint`s for
the CSP. A `Variable` of the CSP takes part in one or multiple `Constraint`s. A `Constraint` involves some subset
of the variables of a CSP and specifies the allowable combinations of values for that subset. The `Constraint`
trait provides the means to determine whether a constraint is *consistent* and *satisfied* given the set of
dependent variables.

The current state of a CSP is represented using an `Assignment`. An assignment always contains the full set of
variables of the CSP. An `Assignment` is *partial*, if it still contains `Variable`s that are unassigned. An assignment
that does not violate any constraints is called *consistent* or *legal*. A *complete* assignment is one in which
every variable is assigned. A solution to the CSP is a *complete* assignment which does not violate any constraints.
Class `Assignment` implements *forward checking*. This is a technique that eliminates the value assigned to a variable
from all other variables that participate in the same `Constraint`s, thus further decreasing the search space of CSP.

Class `DFSSolver` provides a generic way to operate on instances of `CSP` using depth-first search. It is able to apply 
heuristics for both variable ordering and value ordering that can dramatically reduce the search space. By default, it
uses an uninformed approach that simply selects the next unassigned variable and the preserves the original ordering of 
domain values for that variable. The `DFSSolver` progresses from `Assignment` to its successor until a complete 
assignment has been found or the search space is exhausted.

## Constraints

The framework currently only provides the `AllDiff` constraint. This constraint is satisfied if each of its variables is
assigned to a different value.

## Variable Ordering

The `DFSSolver` uses an uninformed approach by default which simply selects the next unassigned variable. However,
it is also possible to use the `MinimumRemainingValue` heuristic, which selects the variable that is most constrained 
given the current state of the CSP. Thus, the variable that has the fewest choices for domain values left will be
chosen. Using this heuristic can dramatically reduce the runtime of the solver, since the search space is pruned
efficiently.

## Value Ordering

The `DFSSolver` uses an uninformed approach by default which simply preserves the original ordering of domain values
for a given unassigned `Variable`. Currently, there is no informed implementation of `ValueOrdering`.

# Noteworthy things about this port

The experience of rewriting a Java-based framework that makes heavy use of functional concepts into an equivalent
Scala solution was quite interesting. I have to say that I was and still am pretty happy on how the Java-based
solution turned out. Using the Stream API and concepts from functional programming like immutability and combinator
functions significantly improved the quality of the code.

Still, the Java version has some shortcomings which the Scala-based solution solves much more elegantly. First,
there is the need to do an excessive amount of defensive copying in order to implement immutable classes. Second,
the Stream API lacks combinator functions like `foldLeft`, which greatly simplify multiple state mutations in the
presence of immutable data structures.

While the Scala version resembles the Java version in almost every details, these aspects improved the readability
of the code even more. The next couple of sections highlight some of the aspects that contribute to the Java vs.
Scala discussion in greater detail, but also provide general things about this port that may be of interest.

## Use a type alias to represent a typed identity

The Java version uses the immutable class `VariableIdentity` to represent the identity of a variable using a specific 
type rather than `String`. But the extra class increases the amount of code and is at its core just a wrapper around a 
`String`. The Scala version makes use of type aliases which eliminates the need for a separate class while enabling us 
to use a specific type.

So, in Scala we simply write

    object Variable {
      type Identity = String
    }

instead of providing our own class for this, like in the Java version.

    public class VariableIdentity {
    
        private final String identity;
    
        private VariableIdentity(final String identity) {
            this.identity = identity;
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
    
            VariableIdentity that = (VariableIdentity) o;
    
            return !(identity != null ? !identity.equals(that.identity) : that.identity != null);
        }
    
        @Override
        public int hashCode() {
            return identity != null ? identity.hashCode() : 0;
        }
    
        @Override
        public String toString() {
            return identity;
        }
    
        public static VariableIdentity id(final String identity) {
            return new VariableIdentity(identity);
        }
    }

## Use case classes with an identity

A `Variable` is represented using a case class to leverage its immutability constraints and copy constructors. 
However, since a variable also has a concept of *identity* the auto-generated methods for `equals` and `hashCode` 
are no longer suitable, since they are evaluated against the whole set of attributes that are declared with the
*first* parameter list of the case class. Splitting the set of attributes into two parameter lists allows us
to implement case classes that have an identity, while retaining their immutability constraints. Have a look at
the implementation of `Variable`.

    case class Variable[+A](id: Identity)(val value: Option[A] = None, val domain: List[A]) {
      def restrict[B >: A](disallowedValue: B): Variable[B] =
        copy()(value = value, domain = domain.filterNot(items => items == disallowedValue))

      def assign[B >: A](value: B): Variable[B] =
        copy()(value = Some(value), domain = List.empty)
    
      def isAssigned(): Boolean =
        value.isDefined
    }

This comes at the price of a slightly higher level of verbosity. Attributes of the case class that go into the second 
parameter have to be explicitly marked as `val`s. Otherwise, accessing the attribute like `Variable.value` would not 
work. Also, copy constructors get a bit more complicated, since the whole set of attributes that went into the second 
parameter list (in this case `value` and `domain`) need to be explicitly named.

## Use of `foldLeft` when applying multiple state mutations

When restricting the domains of dependent variables, we are forced to use a `for`-loop in the Java version in order
to successively mutate the intermediary `Assignment`s since the Java Stream API does not provide a function that acts 
as a reducer and combiner.

    for (VariableIdentity variableIdentity : dependentVariables(variable, constraints)) {
        assignment = assignment.restrict(variableIdentity, value);
    }
    
Scala does provide such a function: `foldLeft`. So instead of mixing imperative and functional programming like in the
Java version, we can simply write

    dependentVariables(variableIdentity, constraints)
      .foldLeft(baseAssignment)((assignment, dependentVariableIdentity) => assignment.restrict(dependentVariableIdentity, value))

and stay functional all the way through.

## Set is invariant in its parametric type

The immutable `Set` that comes with Scala is invariant in its parametric type. Thus, I switched from the immutable 
`Set` to an immutable `List` whenever appropriate. I'd be glad on feedback if there is a better way of doing this.

# Example Application: Sudoku as CSP

Package `com.mgu.csp.sudoku` formulates Sudoku as a constraint satisfaction problem. The current implementation is able to parse
a Sudoku puzzle as line-delimited string like the one shown underneath.
 
    003020600
    900305001
    001806400
    008102900
    700000008
    006708200
    002609500
    800203009
    005010300
    
Each cell of the Sudoku puzzle is represented as a variable of the CSP, where
`0` denotes an unassigned variable with domain values ranging from 1 to 9 and where every other number represents an
assigned variable. There are three kinds of constraints, which are all represented using `AllDiff` on their dependent
variables:

* Row constraints: The assigned values to every variable in a row of the puzzle must be all different.
* Column constraints: The assigned values to every variable in a column of the puzzle must be all different.
* Grid constraints: The assigned values to every variable within a grid must be all different.

In total there are 27 constraints and 81 variables.

# License

This software is released under the terms of the MIT license.
