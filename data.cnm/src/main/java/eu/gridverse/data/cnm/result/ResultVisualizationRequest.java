package eu.gridverse.data.cnm.result;
import java.util.Map; import java.util.UUID;
public record ResultVisualizationRequest(UUID workflowId, Map<String, Double> before, Map<String, Double> after) { public ResultVisualizationRequest { before = before == null ? Map.of() : Map.copyOf(before); after = after == null ? Map.of() : Map.copyOf(after); } }
