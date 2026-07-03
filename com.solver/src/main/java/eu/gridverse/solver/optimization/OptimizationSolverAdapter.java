package eu.gridverse.solver.optimization;
import eu.gridverse.solver.model.NeutralOptimizationProblem;
import eu.gridverse.solver.model.NeutralOptimizationResult;
public interface OptimizationSolverAdapter { NeutralOptimizationResult solve(NeutralOptimizationProblem problem); }
