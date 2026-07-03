package eu.gridverse.data.cnm.rao;

import eu.gridverse.data.cnm.remedialaction.CnmRemedialAction;
import java.util.List;

public record RemedialActionResult(String status, double objectiveValue, List<CnmRemedialAction> proposedActions, List<String> messages) {}
