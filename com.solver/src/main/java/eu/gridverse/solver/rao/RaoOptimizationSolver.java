package eu.gridverse.solver.rao;
import eu.gridverse.data.cnm.rao.RemedialActionRequest;
import eu.gridverse.data.cnm.rao.RemedialActionResult;
public interface RaoOptimizationSolver { RemedialActionResult optimize(RemedialActionRequest request); }
