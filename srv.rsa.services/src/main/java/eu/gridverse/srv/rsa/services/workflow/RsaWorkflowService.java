package eu.gridverse.srv.rsa.services.workflow;
import eu.gridverse.data.cnm.remedialaction.*; import eu.gridverse.data.cnm.result.*; import eu.gridverse.data.cnm.rsa.*; import eu.gridverse.infra.event.EventPublisherService; import eu.gridverse.infra.metadata.MetadataRepository; import eu.gridverse.powsbl.network.NetworkUpdateAdapter;
import java.time.Instant; import java.util.*;
public final class RsaWorkflowService {
    private final NetworkUpdateAdapter networks; private final MetadataRepository<WorkflowMetadata, UUID> workflows; private final EventPublisherService events;
    public RsaWorkflowService(NetworkUpdateAdapter networks, MetadataRepository<WorkflowMetadata, UUID> workflows, EventPublisherService events) { this.networks = networks; this.workflows = workflows; this.events = events; }
    public WorkflowMetadata create(String networkId) { WorkflowMetadata value = new WorkflowMetadata(UUID.randomUUID(), networkId, RsaWorkflowState.CREATED, null, Instant.now()); workflows.save(value); events.publish("rsa.workflow.created", value.id().toString(), value); return value; }
    public ApplyRemedialActionsResult apply(UUID workflowId, List<CnmRemedialAction> actions) {
        WorkflowMetadata workflow = find(workflowId); ApplyRemedialActionsResult result = networks.apply(new ApplyRemedialActionsRequest(workflow.networkId(), actions));
        WorkflowMetadata updated = new WorkflowMetadata(workflow.id(), workflow.networkId(), RsaWorkflowState.ACTIONS_APPLIED, result.updatedNetworkId(), Instant.now()); workflows.save(updated); events.publish("rsa.actions.applied", workflowId.toString(), updated); return result;
    }
    public ResultVisualizationData compare(ResultVisualizationRequest request) {
        Set<String> ids = new TreeSet<>(); ids.addAll(request.before().keySet()); ids.addAll(request.after().keySet()); List<String> improved = new ArrayList<>(); List<String> worsened = new ArrayList<>();
        for (String id : ids) { double before = Math.abs(request.before().getOrDefault(id, 0.0)); double after = Math.abs(request.after().getOrDefault(id, 0.0)); if (after < before) improved.add(id); else if (after > before) worsened.add(id); }
        WorkflowMetadata workflow = find(request.workflowId()); workflows.save(new WorkflowMetadata(workflow.id(), workflow.networkId(), RsaWorkflowState.COMPLETED, workflow.updatedNetworkId(), Instant.now()));
        return new ResultVisualizationData(request.workflowId(), request.before(), request.after(), improved, worsened);
    }
    public WorkflowMetadata find(UUID id) { return workflows.findById(id).orElseThrow(() -> new IllegalArgumentException("workflow not found: " + id)); }
}
