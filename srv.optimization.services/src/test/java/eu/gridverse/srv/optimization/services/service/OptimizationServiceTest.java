package eu.gridverse.srv.optimization.services.service;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
import eu.gridverse.data.cnm.rao.*; import eu.gridverse.solver.model.NeutralOptimizationResult; import eu.gridverse.solver.optimization.OptimizationSolverAdapter; import eu.gridverse.solver.rao.PreventiveRaoProblemMapper;
import java.util.*; import org.junit.jupiter.api.Test; import eu.gridverse.srv.optimization.services.profile.PstProfilePackageParser;
class OptimizationServiceTest {
    @Test void mapsOptimalTapToProposedAction() {
        var solver = mock(OptimizationSolverAdapter.class); when(solver.solve(any())).thenReturn(new NeutralOptimizationResult(NeutralOptimizationResult.Status.OPTIMAL, 3, Map.of("tap__D1__PST", 2.0), List.of()));
        var pst = new PstCandidate("p", "PST", "TSO", 0, 0, 0, -3, 3, -5, 5, 1, null, true); var branch = new MonitoredBranch("b", "LINE", 120, 100, Map.of("PST", -10.0));
        var request = new RemedialActionRequest("n", "D1", List.of(new RaoTimeframeInput("D1", List.of(pst), List.of(branch), Map.of())), List.of(), new RaoParameters(1000, 1, 0, 0, 0));
        var result = new OptimizationService(solver, new PreventiveRaoProblemMapper(), new PstProfilePackageParser()).optimize(request); assertEquals(1, result.proposedActions().size()); assertEquals(2, result.proposedActions().getFirst().setpoints().get("tap"));
    }
}
