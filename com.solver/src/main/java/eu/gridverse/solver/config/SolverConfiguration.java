package eu.gridverse.solver.config;
import java.time.Duration;
public record SolverConfiguration(String executable, Duration timeLimit, double gapTolerance, int threads, boolean fallbackEnabled) {
    public SolverConfiguration { if (executable == null || executable.isBlank()) throw new IllegalArgumentException("solver executable is required"); }
}
