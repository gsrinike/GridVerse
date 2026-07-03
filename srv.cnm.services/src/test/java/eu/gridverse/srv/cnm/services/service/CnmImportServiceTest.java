package eu.gridverse.srv.cnm.services.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import eu.gridverse.data.cnm.common.ImportState;
import eu.gridverse.infra.event.EventPublisherService;
import eu.gridverse.infra.storage.document.DocumentRepositoryService;
import eu.gridverse.infra.storage.object.ObjectStorageService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import eu.gridverse.powsbl.network.CnmNetworkImportAdapter; import eu.gridverse.data.cnm.cgmes.*; import eu.gridverse.data.cnm.iidm.CnmNetworkSummary;
class CnmImportServiceTest {
    @Test void storesAndPublishes() {
        var objects = mock(ObjectStorageService.class); var documents = mock(DocumentRepositoryService.class); var events = mock(EventPublisherService.class);
        var networks = mock(CnmNetworkImportAdapter.class); UUID id = UUID.randomUUID(); when(networks.importNetwork(any())).thenReturn(new NetworkImportResult(id,true,"n",new CnmNetworkSummary("n",0,0,0,0,0,0,0),java.util.List.of()));
        var outcome = new CnmImportService(objects, documents, events, networks).ingest(id, "20250101T0000Z_1D_TSO_EQUIPMENT_v1.xml", "<rdf/>".getBytes());
        assertEquals(ImportState.STORED, outcome.importStatus().state()); verify(events).publish(eq("cnm.import.completed"), eq(id.toString()), any());
    }
}
