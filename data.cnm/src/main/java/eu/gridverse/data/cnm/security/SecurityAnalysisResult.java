package eu.gridverse.data.cnm.security;

import java.util.List;

public record SecurityAnalysisResult(boolean successful, List<CnmViolation> violations, List<String> failedContingencies) {
    public SecurityAnalysisResult { violations = violations == null ? List.of() : List.copyOf(violations); failedContingencies = failedContingencies == null ? List.of() : List.copyOf(failedContingencies); }
}
