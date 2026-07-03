package eu.gridverse.powsbl.sensitivity;

import com.powsybl.sensitivity.*;
import eu.gridverse.data.cnm.sensitivity.SensitivityAnalysisRequest;
import eu.gridverse.data.cnm.sensitivity.SensitivityAnalysisResult;
import eu.gridverse.powsbl.PowerAnalysisException;
import eu.gridverse.powsbl.config.PowsyblNetworkRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public final class PowsyblSensitivityAnalysisAdapter implements SensitivityAnalysisAdapter {
    private final PowsyblNetworkRepository repository;
    public PowsyblSensitivityAnalysisAdapter(PowsyblNetworkRepository repository) { this.repository = repository; }
    @Override public SensitivityAnalysisResult run(SensitivityAnalysisRequest request) {
        try {
            var factors = new ArrayList<SensitivityFactor>();
            for (String branch : request.monitoredElements()) for (String variable : request.variables()) factors.add(new SensitivityFactor(SensitivityFunctionType.BRANCH_CURRENT_1, branch, SensitivityVariableType.TRANSFORMER_PHASE, variable, false, com.powsybl.contingency.ContingencyContext.all()));
            var result = SensitivityAnalysis.run(repository.get(request.networkId()), factors); var mapped = new LinkedHashMap<String, java.util.Map<String, Double>>();
            for (var value : result.getPreContingencyValues()) { var factor = factors.get(value.getFactorIndex()); mapped.computeIfAbsent(factor.getFunctionId(), ignored -> new LinkedHashMap<>()).put(factor.getVariableId(), value.getValue()); }
            return new SensitivityAnalysisResult(true, mapped);
        } catch (RuntimeException exception) { throw new PowerAnalysisException("sensitivity analysis failed", exception); }
    }
}
