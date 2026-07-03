package eu.gridverse.srv.computation.services.service;
import eu.gridverse.data.cnm.loadflow.*; import eu.gridverse.data.cnm.rsa.*; import eu.gridverse.data.cnm.security.*; import eu.gridverse.data.cnm.sensitivity.*;
import eu.gridverse.powsbl.loadflow.LoadFlowAdapter; import eu.gridverse.powsbl.security.SecurityAnalysisAdapter; import eu.gridverse.powsbl.sensitivity.SensitivityAnalysisAdapter;
import java.util.Map;
public final class ComputationService {
    private final LoadFlowAdapter loadFlow; private final SecurityAnalysisAdapter security; private final SensitivityAnalysisAdapter sensitivity;
    public ComputationService(LoadFlowAdapter loadFlow, SecurityAnalysisAdapter security, SensitivityAnalysisAdapter sensitivity) { this.loadFlow = loadFlow; this.security = security; this.sensitivity = sensitivity; }
    public LoadFlowResult loadFlow(LoadFlowRequest request) { return loadFlow.run(request); }
    public SecurityAnalysisResult security(SecurityAnalysisRequest request) { return security.run(request); }
    public SensitivityAnalysisResult sensitivity(SensitivityAnalysisRequest request) { return sensitivity.run(request); }
    public RerunAnalysisResult rerun(RerunAnalysisRequest request) {
        LoadFlowResult lf = loadFlow.run(new LoadFlowRequest(request.networkId(), false, Map.of()));
        if (!lf.converged()) return new RerunAnalysisResult(lf, new SecurityAnalysisResult(false, java.util.List.of(), java.util.List.of("load flow did not converge")));
        return new RerunAnalysisResult(lf, security.run(new SecurityAnalysisRequest(request.networkId(), request.contingencies())));
    }
}
