package eu.gridverse.solver.scip;
import static org.junit.jupiter.api.Assertions.assertTrue;
import eu.gridverse.solver.model.NeutralOptimizationProblem;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
class LpFileWriterTest {
    @Test void writesIntegerLinearProgram() {
        var problem = new NeutralOptimizationProblem(List.of(new NeutralOptimizationProblem.Variable("tap", -2, 2, true)), List.of(new NeutralOptimizationProblem.Constraint("limit", Map.of("tap", 1.0), NeutralOptimizationProblem.Relation.LESS_OR_EQUAL, 1)), Map.of("tap", 2.0));
        String lp = new LpFileWriter().write(problem); assertTrue(lp.contains("Generals")); assertTrue(lp.contains("limit:"));
    }
}
