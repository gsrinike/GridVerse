package eu.gridverse.data.cnm.rao;
import java.util.Map;
public record RaoAnalysisSnapshot(Map<String, Double> branchCurrentsA, Map<String, Map<String, Double>> currentSensitivities) { public RaoAnalysisSnapshot { branchCurrentsA = branchCurrentsA == null ? Map.of() : Map.copyOf(branchCurrentsA); currentSensitivities = currentSensitivities == null ? Map.of() : Map.copyOf(currentSensitivities); } }
