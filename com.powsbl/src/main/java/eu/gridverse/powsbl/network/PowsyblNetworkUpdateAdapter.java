package eu.gridverse.powsbl.network;

import eu.gridverse.data.cnm.remedialaction.ApplyRemedialActionsRequest;
import eu.gridverse.data.cnm.remedialaction.ApplyRemedialActionsResult;
import eu.gridverse.powsbl.config.PowsyblNetworkRepository;
import java.util.ArrayList;

public final class PowsyblNetworkUpdateAdapter implements NetworkUpdateAdapter {
    private final PowsyblNetworkRepository repository;
    public PowsyblNetworkUpdateAdapter(PowsyblNetworkRepository repository) { this.repository = repository; }
    @Override public ApplyRemedialActionsResult apply(ApplyRemedialActionsRequest request) {
        var network = repository.get(request.networkId()); var applied = new ArrayList<String>(); var rejected = new ArrayList<String>();
        for (var action : request.actions()) {
            var transformer = network.getTwoWindingsTransformer(action.id()); Double tap = action.setpoints().get("tap");
            if (transformer != null && transformer.getPhaseTapChanger() != null && tap != null) { transformer.getPhaseTapChanger().setTapPosition(tap.intValue()); applied.add(action.id()); } else rejected.add(action.id());
        }
        repository.put(request.networkId(), network); return new ApplyRemedialActionsResult(request.networkId(), applied, rejected);
    }
}
