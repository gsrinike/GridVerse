package eu.gridverse.solver.scip;

import eu.gridverse.solver.config.SolverConfiguration;
import eu.gridverse.solver.model.NeutralOptimizationProblem;
import eu.gridverse.solver.model.NeutralOptimizationResult;
import eu.gridverse.solver.optimization.OptimizationSolverAdapter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ScipCommandSolverAdapter implements OptimizationSolverAdapter {
    private final SolverConfiguration configuration;
    public ScipCommandSolverAdapter(SolverConfiguration configuration) { this.configuration = configuration; }
    @Override public NeutralOptimizationResult solve(NeutralOptimizationProblem problem) {
        try {
            var directory = Files.createTempDirectory("gridverse-scip-"); var model = directory.resolve("problem.lp"); var solution = directory.resolve("solution.sol");
            Files.writeString(model, new LpFileWriter().write(problem));
            List<String> command = new ArrayList<>(List.of(configuration.executable(), "-c", "read " + model, "-c", "set limits time " + configuration.timeLimit().toSeconds(), "-c", "set limits gap " + configuration.gapTolerance(), "-c", "set parallel maxnthreads " + configuration.threads(), "-c", "optimize", "-c", "write solution " + solution, "-c", "quit"));
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            boolean finished = process.waitFor(configuration.timeLimit().plus(Duration.ofSeconds(5)).toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) { process.destroyForcibly(); return new NeutralOptimizationResult(NeutralOptimizationResult.Status.TIMEOUT, Double.NaN, java.util.Map.of(), List.of("SCIP time limit exceeded")); }
            String output = new String(process.getInputStream().readAllBytes()); var status = ScipStatusTranslator.translate(output, false, process.exitValue());
            var values = new LinkedHashMap<String, Double>(); double objective = Double.NaN;
            if (Files.exists(solution)) for (String line : Files.readAllLines(solution)) {
                String trimmed = line.trim(); if (trimmed.startsWith("objective value:")) objective = Double.parseDouble(trimmed.substring(trimmed.indexOf(':') + 1).trim());
                else if (!trimmed.isEmpty() && !trimmed.startsWith("solution status:") && !trimmed.startsWith("#")) { String[] parts = trimmed.split("\\s+"); if (parts.length >= 2) try { values.put(parts[0], Double.parseDouble(parts[1])); } catch (NumberFormatException ignored) {} }
            }
            return new NeutralOptimizationResult(status, objective, values, List.of(output));
        } catch (IOException exception) {
            return new NeutralOptimizationResult(NeutralOptimizationResult.Status.ERROR, Double.NaN, java.util.Map.of(), List.of("SCIP execution failed: " + exception.getMessage()));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt(); return new NeutralOptimizationResult(NeutralOptimizationResult.Status.ERROR, Double.NaN, java.util.Map.of(), List.of("SCIP execution interrupted"));
        }
    }
}
