package eu.gridverse.data.cnm.loadflow;

import java.util.List;
import java.util.Map;

public record LoadFlowResult(boolean converged, int iterations, Map<String, Double> branchFlowsMw, Map<String, Double> branchCurrentsA, List<String> messages) {
    public LoadFlowResult { branchFlowsMw = branchFlowsMw == null ? Map.of() : Map.copyOf(branchFlowsMw); branchCurrentsA = branchCurrentsA == null ? Map.of() : Map.copyOf(branchCurrentsA); messages = messages == null ? List.of() : List.copyOf(messages); }
}
