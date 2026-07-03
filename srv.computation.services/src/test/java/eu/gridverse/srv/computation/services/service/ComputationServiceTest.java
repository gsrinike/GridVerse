package eu.gridverse.srv.computation.services.service;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
import eu.gridverse.data.cnm.loadflow.*; import eu.gridverse.data.cnm.rsa.*; import eu.gridverse.powsbl.loadflow.LoadFlowAdapter; import eu.gridverse.powsbl.security.SecurityAnalysisAdapter; import eu.gridverse.powsbl.sensitivity.SensitivityAnalysisAdapter;
import java.util.*; import org.junit.jupiter.api.Test;
class ComputationServiceTest {
    @Test void stopsRerunWhenLoadFlowFails() {
        var lf = mock(LoadFlowAdapter.class); var sa = mock(SecurityAnalysisAdapter.class); when(lf.run(any())).thenReturn(new LoadFlowResult(false, 2, Map.of(), Map.of(), List.of("failed")));
        var result = new ComputationService(lf, sa, mock(SensitivityAnalysisAdapter.class)).rerun(new RerunAnalysisRequest("n", List.of()));
        assertFalse(result.securityAnalysis().successful()); verifyNoInteractions(sa);
    }
}
