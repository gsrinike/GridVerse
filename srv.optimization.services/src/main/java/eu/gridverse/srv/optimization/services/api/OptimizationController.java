package eu.gridverse.srv.optimization.services.api;
import com.fasterxml.jackson.databind.ObjectMapper; import eu.gridverse.data.cnm.rao.*; import eu.gridverse.srv.optimization.services.service.OptimizationService; import java.io.IOException; import org.springframework.http.MediaType; import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/api/v1/optimizations") public class OptimizationController {
    private final OptimizationService service; private final ObjectMapper json; public OptimizationController(OptimizationService service, ObjectMapper json) { this.service = service; this.json = json; }
    @PostMapping("/rao") public RemedialActionResult optimize(@RequestBody RemedialActionRequest request) { return service.optimize(request); }
    @PostMapping(value="/rao/profile-package",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public RemedialActionResult optimizeProfile(@RequestParam String networkId,@RequestParam String timestamp,@RequestPart MultipartFile profilePackage,@RequestParam String snapshot) throws IOException { return service.optimizeProfilePackage(networkId,timestamp,profilePackage.getBytes(),json.readValue(snapshot,RaoAnalysisSnapshot.class)); }
}
