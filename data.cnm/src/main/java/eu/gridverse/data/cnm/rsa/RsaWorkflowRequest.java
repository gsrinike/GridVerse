package eu.gridverse.data.cnm.rsa;

import eu.gridverse.data.cnm.remedialaction.CnmRemedialAction;
import eu.gridverse.data.cnm.security.CnmContingency;
import java.util.List;

public record RsaWorkflowRequest(String networkId, List<CnmContingency> contingencies, List<CnmRemedialAction> candidateActions) {}
