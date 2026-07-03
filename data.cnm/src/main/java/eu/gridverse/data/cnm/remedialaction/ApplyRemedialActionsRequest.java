package eu.gridverse.data.cnm.remedialaction;

import java.util.List;

public record ApplyRemedialActionsRequest(String networkId, List<CnmRemedialAction> actions) {
    public ApplyRemedialActionsRequest { actions = actions == null ? List.of() : List.copyOf(actions); }
}
