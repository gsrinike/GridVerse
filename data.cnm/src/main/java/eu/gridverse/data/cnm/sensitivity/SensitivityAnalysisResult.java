package eu.gridverse.data.cnm.sensitivity;

import java.util.Map;

public record SensitivityAnalysisResult(boolean successful, Map<String, Map<String, Double>> factors) {
    public SensitivityAnalysisResult { factors = factors == null ? Map.of() : Map.copyOf(factors); }
}
