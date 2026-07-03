package eu.gridverse.srv.cnm.services.service;

import eu.gridverse.data.cnm.common.ImportFileState;
import eu.gridverse.data.cnm.common.ImportFileStatus;
import eu.gridverse.data.cnm.common.ImportState;
import eu.gridverse.data.cnm.common.ImportStatus;
import eu.gridverse.data.cnm.cgmes.CnmImportOutcome;
import eu.gridverse.data.cnm.cgmes.NetworkImportRequest;
import eu.gridverse.infra.event.EventPublisherService;
import eu.gridverse.infra.storage.document.DocumentRepositoryService;
import eu.gridverse.infra.storage.object.ObjectStorageService;
import eu.gridverse.srv.cnm.services.rdf.ProfileFilenameParser;
import eu.gridverse.srv.cnm.services.rdf.SafeArchiveExpander;
import eu.gridverse.powsbl.network.CnmNetworkImportAdapter;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CnmImportService {
    private final ObjectStorageService objects; private final DocumentRepositoryService documents; private final EventPublisherService events; private final CnmNetworkImportAdapter networks;
    private final SafeArchiveExpander expander = new SafeArchiveExpander(); private final ProfileFilenameParser parser = new ProfileFilenameParser();
    public CnmImportService(ObjectStorageService objects, DocumentRepositoryService documents, EventPublisherService events, CnmNetworkImportAdapter networks) { this.objects = objects; this.documents = documents; this.events = events; this.networks = networks; }
    public CnmImportOutcome ingest(UUID importId, String fileName, byte[] content) {
        var statuses = new ArrayList<ImportFileStatus>();
        try {
            for (var payload : expander.expand(fileName, content)) {
                String key = importId + "/" + payload.name();
                objects.put("cnm-raw", key, new ByteArrayInputStream(payload.content()), payload.content().length, "application/rdf+xml");
                parser.parse(payload.name(), key).ifPresent(metadata -> documents.save("cnm-profiles", key, metadata));
                statuses.add(new ImportFileStatus(payload.name(), ImportFileState.STORED, payload.content().length, "stored"));
            }
            String suffix = fileName != null && fileName.toLowerCase().endsWith(".zip") ? ".zip" : ".xml"; Path staged = Files.createTempFile("gridverse-cgmes-", suffix); Files.write(staged, content);
            var network = networks.importNetwork(new NetworkImportRequest(importId, java.util.List.of(staged.toString()))); Files.deleteIfExists(staged);
            ImportStatus status = new ImportStatus(importId, ImportState.STORED, statuses, Instant.now());
            documents.save("cnm-imports", importId.toString(), status);
            events.publish("cnm.import.completed", importId.toString(), status);
            return new CnmImportOutcome(status, network);
        } catch (java.io.IOException | RuntimeException exception) {
            ImportStatus status = new ImportStatus(importId, ImportState.FAILED, statuses, Instant.now());
            documents.save("cnm-imports", importId.toString(), status);
            throw exception instanceof RuntimeException runtime ? runtime : new IllegalStateException("could not stage CGMES input", exception);
        }
    }
}
