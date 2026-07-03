package eu.gridverse.srv.rsa.services.workflow;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*; import eu.gridverse.data.cnm.result.*; import eu.gridverse.infra.event.EventPublisherService; import eu.gridverse.infra.metadata.MetadataRepository; import eu.gridverse.powsbl.network.NetworkUpdateAdapter; import java.util.*; import org.junit.jupiter.api.Test;
class RsaWorkflowServiceTest {
    @Test void comparisonClassifiesChanges() {
        @SuppressWarnings("unchecked") MetadataRepository<WorkflowMetadata, UUID> metadata = mock(MetadataRepository.class); UUID id = UUID.randomUUID(); when(metadata.findById(id)).thenReturn(Optional.of(new WorkflowMetadata(id, "n", eu.gridverse.data.cnm.rsa.RsaWorkflowState.RERUN_DONE, "n2", java.time.Instant.now())));
        var result = new RsaWorkflowService(mock(NetworkUpdateAdapter.class), metadata, mock(EventPublisherService.class)).compare(new ResultVisualizationRequest(id, Map.of("a", 120.0, "b", 80.0), Map.of("a", 90.0, "b", 95.0)));
        assertEquals(List.of("a"), result.improvedElements()); assertEquals(List.of("b"), result.worsenedElements());
    }
}
