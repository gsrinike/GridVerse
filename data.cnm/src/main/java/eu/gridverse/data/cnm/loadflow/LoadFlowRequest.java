package eu.gridverse.data.cnm.loadflow;

import java.util.Map;

public record LoadFlowRequest(String networkId, boolean dc, Map<String, String> parameters) {
    public LoadFlowRequest { parameters = parameters == null ? Map.of() : Map.copyOf(parameters); }
}
