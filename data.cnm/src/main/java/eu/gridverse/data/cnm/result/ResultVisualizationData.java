package eu.gridverse.data.cnm.result;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ResultVisualizationData(UUID workflowId, Map<String, Double> before, Map<String, Double> after, List<String> improvedElements, List<String> worsenedElements) {}
