package eu.gridverse.data.cnm.remedialaction;

import java.util.List;

public record ApplyRemedialActionsResult(String updatedNetworkId, List<String> appliedActionIds, List<String> rejectedActionIds) {}
