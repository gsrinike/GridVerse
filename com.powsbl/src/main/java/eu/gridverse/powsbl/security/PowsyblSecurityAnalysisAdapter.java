package eu.gridverse.powsbl.security;

import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.BranchContingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.security.SecurityAnalysis;
import eu.gridverse.data.cnm.security.CnmViolation;
import eu.gridverse.data.cnm.security.SecurityAnalysisRequest;
import eu.gridverse.data.cnm.security.SecurityAnalysisResult;
import eu.gridverse.powsbl.PowerAnalysisException;
import eu.gridverse.powsbl.config.PowsyblNetworkRepository;
import java.util.ArrayList;
import java.util.List;

public final class PowsyblSecurityAnalysisAdapter implements SecurityAnalysisAdapter {
    private final PowsyblNetworkRepository repository;
    public PowsyblSecurityAnalysisAdapter(PowsyblNetworkRepository repository) { this.repository = repository; }
    @Override public SecurityAnalysisResult run(SecurityAnalysisRequest request) {
        try {
            List<Contingency> contingencies = request.contingencies().stream()
                    .map(c -> new Contingency(c.id(), c.elementIds().stream()
                            .<ContingencyElement>map(BranchContingency::new).toList()))
                    .toList();
            var result = SecurityAnalysis.run(repository.get(request.networkId()), contingencies).getResult(); var violations = new ArrayList<CnmViolation>(); var failed = new ArrayList<String>();
            result.getPostContingencyResults().forEach(post -> {
                if (post.getStatus() != com.powsybl.security.PostContingencyComputationStatus.CONVERGED) failed.add(post.getContingency().getId());
                post.getLimitViolationsResult().getLimitViolations().forEach(v -> violations.add(new CnmViolation(post.getContingency().getId(), v.getSubjectId(), v.getLimitType().name(), v.getValue(), v.getLimit(), v.getLimit() == 0 ? 0 : 100 * (Math.abs(v.getValue()) - v.getLimit()) / v.getLimit())));
            });
            return new SecurityAnalysisResult(failed.isEmpty(), violations, failed);
        } catch (RuntimeException exception) { throw new PowerAnalysisException("security analysis failed", exception); }
    }
}
