package eu.gridverse.data.cnm.rsa;
import eu.gridverse.data.cnm.loadflow.LoadFlowResult;
import eu.gridverse.data.cnm.security.SecurityAnalysisResult;
public record RerunAnalysisResult(LoadFlowResult loadFlow, SecurityAnalysisResult securityAnalysis) {}
