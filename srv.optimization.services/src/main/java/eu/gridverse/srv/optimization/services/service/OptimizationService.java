package eu.gridverse.srv.optimization.services.service;
import eu.gridverse.data.cnm.rao.*; import eu.gridverse.data.cnm.remedialaction.CnmRemedialAction; import eu.gridverse.solver.model.NeutralOptimizationResult; import eu.gridverse.solver.optimization.OptimizationSolverAdapter; import eu.gridverse.solver.rao.PreventiveRaoProblemMapper; import eu.gridverse.srv.optimization.services.profile.PstProfilePackageParser;
import java.util.*;
public final class OptimizationService {
    private final OptimizationSolverAdapter solver; private final PreventiveRaoProblemMapper mapper; private final PstProfilePackageParser profiles;
    public OptimizationService(OptimizationSolverAdapter solver, PreventiveRaoProblemMapper mapper, PstProfilePackageParser profiles) { this.solver = solver; this.mapper = mapper; this.profiles = profiles; }
    public RemedialActionResult optimizeProfilePackage(String networkId, String timestamp, byte[] packageBytes, RaoAnalysisSnapshot snapshot) { return optimize(profiles.parse(networkId, timestamp, packageBytes, snapshot)); }
    public RemedialActionResult optimize(RemedialActionRequest request) {
        NeutralOptimizationResult solved = solver.solve(mapper.map(request));
        if (solved.status() != NeutralOptimizationResult.Status.OPTIMAL && solved.status() != NeutralOptimizationResult.Status.FEASIBLE) return new RemedialActionResult(solved.status().name(), solved.objectiveValue(), List.of(), solved.messages());
        RaoTimeframeInput active = request.timeframes().stream().filter(t -> t.timeframe().equals(request.activeTimeframe())).findFirst().orElse(request.timeframes().getFirst());
        List<CnmRemedialAction> actions = new ArrayList<>();
        for (PstCandidate pst : active.psts()) {
            String variable = "tap__" + safe(active.timeframe()) + "__" + safe(pst.label()); Double tap = solved.values().get(variable);
            if (tap != null && Math.round(tap) != pst.currentTap()) actions.add(new CnmRemedialAction(pst.id(), "Set " + pst.label() + " tap", "PST_TAP", Map.of("tap", (double) Math.round(tap)), Math.abs(Math.round(tap) - pst.currentTap()) * pst.costWeight()));
        }
        return new RemedialActionResult(solved.status().name(), solved.objectiveValue(), actions, solved.messages());
    }
    private static String safe(String value) { return value.replaceAll("[^A-Za-z0-9_]", "_"); }
}
