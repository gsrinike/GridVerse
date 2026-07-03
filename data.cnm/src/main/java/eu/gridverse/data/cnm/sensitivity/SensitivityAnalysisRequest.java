package eu.gridverse.data.cnm.sensitivity;

import java.util.List;

public record SensitivityAnalysisRequest(String networkId, List<String> monitoredElements, List<String> variables) {
    public SensitivityAnalysisRequest { monitoredElements = List.copyOf(monitoredElements); variables = List.copyOf(variables); }
}
