package eu.gridverse.solver.model;

import java.util.List;
import java.util.Map;

public record NeutralOptimizationProblem(List<Variable> variables, List<Constraint> constraints, Map<String, Double> objective) {
    public record Variable(String name, double lowerBound, double upperBound, boolean integral) {}
    public record Constraint(String name, Map<String, Double> coefficients, Relation relation, double rightHandSide) {}
    public enum Relation { LESS_OR_EQUAL, EQUAL, GREATER_OR_EQUAL }
}
