package eu.gridverse.solver.scip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import eu.gridverse.solver.model.NeutralOptimizationResult.Status;
import org.junit.jupiter.api.Test;
class ScipStatusTranslatorTest {
    @Test void translatesOptimal() { assertEquals(Status.OPTIMAL, ScipStatusTranslator.translate("optimal solution found", false, 0)); }
    @Test void translatesInfeasible() { assertEquals(Status.INFEASIBLE, ScipStatusTranslator.translate("problem is infeasible", false, 0)); }
    @Test void timeoutWins() { assertEquals(Status.TIMEOUT, ScipStatusTranslator.translate("", true, 0)); }
}
