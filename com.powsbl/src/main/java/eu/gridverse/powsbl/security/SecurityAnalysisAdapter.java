package eu.gridverse.powsbl.security;
import eu.gridverse.data.cnm.security.SecurityAnalysisRequest;
import eu.gridverse.data.cnm.security.SecurityAnalysisResult;
public interface SecurityAnalysisAdapter { SecurityAnalysisResult run(SecurityAnalysisRequest request); }
