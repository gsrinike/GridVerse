package eu.gridverse.solver.scip;
import eu.gridverse.solver.model.NeutralOptimizationResult.Status;
public final class ScipStatusTranslator {
    private ScipStatusTranslator() {}
    public static Status translate(String output, boolean timedOut, int exitCode) {
        if (timedOut) return Status.TIMEOUT;
        String normalized = output == null ? "" : output.toLowerCase();
        if (normalized.contains("infeasible")) return Status.INFEASIBLE;
        if (normalized.contains("optimal solution found")) return Status.OPTIMAL;
        if (normalized.contains("solution found")) return Status.FEASIBLE;
        return exitCode == 0 ? Status.ERROR : Status.ERROR;
    }
}
