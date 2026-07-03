package eu.gridverse.powsbl.loadflow;

import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowParameters;
import eu.gridverse.data.cnm.loadflow.LoadFlowRequest;
import eu.gridverse.data.cnm.loadflow.LoadFlowResult;
import eu.gridverse.powsbl.PowerAnalysisException;
import eu.gridverse.powsbl.config.PowsyblNetworkRepository;
import java.util.LinkedHashMap;
import java.util.List;

public final class PowsyblLoadFlowAdapter implements LoadFlowAdapter {
    private final PowsyblNetworkRepository repository;
    public PowsyblLoadFlowAdapter(PowsyblNetworkRepository repository) { this.repository = repository; }
    @Override public LoadFlowResult run(LoadFlowRequest request) {
        try {
            var network = repository.get(request.networkId()); var parameters = LoadFlowParameters.load().setDc(request.dc()); var report = LoadFlow.run(network, parameters);
            var flows = new LinkedHashMap<String, Double>(); var currents = new LinkedHashMap<String, Double>();
            network.getBranchStream().forEach(branch -> { flows.put(branch.getId(), branch.getTerminal1().getP()); currents.put(branch.getId(), Math.max(Math.abs(branch.getTerminal1().getI()), Math.abs(branch.getTerminal2().getI()))); });
            return new LoadFlowResult(report.isOk(), report.getComponentResults().size(), flows, currents, report.isOk() ? List.of() : List.of("load flow did not converge"));
        } catch (RuntimeException exception) { throw new PowerAnalysisException("load flow failed", exception); }
    }
}
