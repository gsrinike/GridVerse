package eu.gridverse.solver.scip;

import eu.gridverse.solver.model.NeutralOptimizationProblem;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

public final class LpFileWriter {
    public String write(NeutralOptimizationProblem problem) {
        StringBuilder lp = new StringBuilder("Minimize\n objective: ").append(expression(problem.objective())).append("\nSubject To\n");
        for (var constraint : problem.constraints()) lp.append(' ').append(constraint.name()).append(": ").append(expression(constraint.coefficients())).append(' ').append(switch (constraint.relation()) { case LESS_OR_EQUAL -> "<="; case EQUAL -> "="; case GREATER_OR_EQUAL -> ">="; }).append(' ').append(number(constraint.rightHandSide())).append('\n');
        lp.append("Bounds\n");
        for (var variable : problem.variables()) {
            if (Double.isInfinite(variable.lowerBound()) && Double.isInfinite(variable.upperBound())) lp.append(' ').append(variable.name()).append(" free\n");
            else lp.append(' ').append(number(variable.lowerBound())).append(" <= ").append(variable.name()).append(" <= ").append(number(variable.upperBound())).append('\n');
        }
        var integers = problem.variables().stream().filter(NeutralOptimizationProblem.Variable::integral).toList();
        if (!integers.isEmpty()) { lp.append("Generals\n"); integers.forEach(v -> lp.append(' ').append(v.name()).append('\n')); }
        return lp.append("End\n").toString();
    }
    private static String expression(Map<String, Double> terms) {
        StringJoiner joiner = new StringJoiner(" ");
        terms.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> { double v = entry.getValue(); if (v != 0) joiner.add((v >= 0 ? "+ " : "- ") + number(Math.abs(v)) + " " + entry.getKey()); });
        String value = joiner.toString(); return value.isEmpty() ? "0" : value.startsWith("+ ") ? value.substring(2) : value;
    }
    private static String number(double value) { if (value == Double.POSITIVE_INFINITY) return "+inf"; if (value == Double.NEGATIVE_INFINITY) return "-inf"; return String.format(Locale.ROOT, "%.12g", value); }
}
