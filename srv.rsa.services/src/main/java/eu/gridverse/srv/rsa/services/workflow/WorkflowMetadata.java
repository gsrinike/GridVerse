package eu.gridverse.srv.rsa.services.workflow;
import eu.gridverse.data.cnm.rsa.RsaWorkflowState; import java.time.Instant; import java.util.UUID;
public record WorkflowMetadata(UUID id, String networkId, RsaWorkflowState state, String updatedNetworkId, Instant updatedAt) {}
