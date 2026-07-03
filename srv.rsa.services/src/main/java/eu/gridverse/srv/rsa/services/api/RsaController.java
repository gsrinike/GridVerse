package eu.gridverse.srv.rsa.services.api;
import eu.gridverse.data.cnm.remedialaction.*; import eu.gridverse.data.cnm.result.*; import eu.gridverse.srv.rsa.services.workflow.*; import java.util.List; import java.util.UUID; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/rsa/workflows") public class RsaController {
    private final RsaWorkflowService service; public RsaController(RsaWorkflowService service) { this.service = service; }
    @PostMapping public WorkflowMetadata create(@RequestParam String networkId) { return service.create(networkId); }
    @GetMapping("/{id}") public WorkflowMetadata get(@PathVariable UUID id) { return service.find(id); }
    @PostMapping("/{id}/actions") public ApplyRemedialActionsResult apply(@PathVariable UUID id, @RequestBody List<CnmRemedialAction> actions) { return service.apply(id, actions); }
    @PostMapping("/comparison") public ResultVisualizationData compare(@RequestBody ResultVisualizationRequest request) { return service.compare(request); }
}
