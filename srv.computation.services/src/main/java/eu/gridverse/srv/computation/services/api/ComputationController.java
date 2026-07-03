package eu.gridverse.srv.computation.services.api;
import eu.gridverse.data.cnm.loadflow.*; import eu.gridverse.data.cnm.rsa.*; import eu.gridverse.data.cnm.security.*; import eu.gridverse.data.cnm.sensitivity.*; import eu.gridverse.srv.computation.services.service.ComputationService;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/computations") public class ComputationController {
    private final ComputationService service; public ComputationController(ComputationService service) { this.service = service; }
    @PostMapping("/load-flow") public LoadFlowResult loadFlow(@RequestBody LoadFlowRequest request) { return service.loadFlow(request); }
    @PostMapping("/security-analysis") public SecurityAnalysisResult security(@RequestBody SecurityAnalysisRequest request) { return service.security(request); }
    @PostMapping("/sensitivity-analysis") public SensitivityAnalysisResult sensitivity(@RequestBody SensitivityAnalysisRequest request) { return service.sensitivity(request); }
    @PostMapping("/rerun") public RerunAnalysisResult rerun(@RequestBody RerunAnalysisRequest request) { return service.rerun(request); }
}
