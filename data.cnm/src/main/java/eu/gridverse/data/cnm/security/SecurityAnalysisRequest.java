package eu.gridverse.data.cnm.security;

import java.util.List;

public record SecurityAnalysisRequest(String networkId, List<CnmContingency> contingencies) {
    public SecurityAnalysisRequest { contingencies = contingencies == null ? List.of() : List.copyOf(contingencies); }
}
