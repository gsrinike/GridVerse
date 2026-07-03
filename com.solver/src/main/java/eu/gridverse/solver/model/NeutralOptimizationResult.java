package eu.gridverse.solver.model;

import java.util.List;
import java.util.Map;

public record NeutralOptimizationResult(Status status, double objectiveValue, Map<String, Double> values, List<String> messages) {
    public enum Status { OPTIMAL, FEASIBLE, INFEASIBLE, TIMEOUT, ERROR }
}
