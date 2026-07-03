package eu.gridverse.srv.cnm.services.api;

import eu.gridverse.data.cnm.cgmes.CnmImportOutcome;
import eu.gridverse.srv.cnm.services.service.CnmImportService;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequestMapping("/api/v1/cnm/imports")
public class CnmImportController {
    private final CnmImportService service;
    public CnmImportController(CnmImportService service) { this.service = service; }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CnmImportOutcome upload(@RequestParam UUID importId, @RequestParam MultipartFile file) throws IOException { return service.ingest(importId, file.getOriginalFilename(), file.getBytes()); }
}
