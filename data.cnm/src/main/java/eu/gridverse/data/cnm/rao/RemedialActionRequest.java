package eu.gridverse.data.cnm.rao;

import java.util.List;

public record RemedialActionRequest(String networkId, String activeTimeframe, List<RaoTimeframeInput> timeframes, List<PstGroupPair> equalityGroups, RaoParameters parameters) {
    public RemedialActionRequest {
        timeframes = timeframes == null ? List.of() : List.copyOf(timeframes);
        equalityGroups = equalityGroups == null ? List.of() : List.copyOf(equalityGroups);
        if (networkId == null || networkId.isBlank() || timeframes.isEmpty()) throw new IllegalArgumentException("networkId and timeframes are required");
    }
}
