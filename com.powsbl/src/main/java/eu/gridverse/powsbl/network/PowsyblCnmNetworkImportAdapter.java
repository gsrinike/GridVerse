package eu.gridverse.powsbl.network;

import eu.gridverse.data.cnm.cgmes.NetworkImportRequest;
import eu.gridverse.data.cnm.cgmes.NetworkImportResult;
import eu.gridverse.data.cnm.iidm.CnmNetworkSummary;
import eu.gridverse.powsbl.PowerAnalysisException;
import eu.gridverse.powsbl.config.PowsyblNetworkRepository;
import java.util.List;

public final class PowsyblCnmNetworkImportAdapter implements CnmNetworkImportAdapter {
    private final PowsyblNetworkRepository repository;
    public PowsyblCnmNetworkImportAdapter(PowsyblNetworkRepository repository) { this.repository = repository; }
    @Override public NetworkImportResult importNetwork(NetworkImportRequest request) {
        if (request.objectKeys().isEmpty()) throw new IllegalArgumentException("at least one CGMES location is required");
        try {
            var network = repository.importPath(request.objectKeys().getFirst()); String id = repository.identifier(network);
            CnmNetworkSummary summary = new CnmNetworkSummary(id, (int) network.getSubstationStream().count(), (int) network.getVoltageLevelStream().count(), (int) network.getBusView().getBusStream().count(), (int) network.getLineStream().count(), (int) network.getTwoWindingsTransformerStream().count() + (int) network.getThreeWindingsTransformerStream().count(), (int) network.getGeneratorStream().count(), (int) network.getLoadStream().count());
            return new NetworkImportResult(request.importId(), true, id, summary, List.of());
        } catch (RuntimeException exception) { throw new PowerAnalysisException("CGMES import failed", exception); }
    }
}
