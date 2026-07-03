package eu.gridverse.data.cnm.rsa;

import eu.gridverse.data.cnm.loadflow.LoadFlowResult;
import eu.gridverse.data.cnm.rao.RemedialActionResult;
import eu.gridverse.data.cnm.security.SecurityAnalysisResult;
import java.time.Instant;
import java.util.UUID;

public record RsaWorkflowResult(UUID workflowId, RsaWorkflowState state, LoadFlowResult baselineLoadFlow, SecurityAnalysisResult baselineSecurity, RemedialActionResult optimization, LoadFlowResult finalLoadFlow, SecurityAnalysisResult finalSecurity, Instant updatedAt) {}
