package eu.gridverse.powsbl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import eu.gridverse.powsbl.loadflow.LoadFlowAdapter;
import org.junit.jupiter.api.Test;

class AdapterBoundaryTest {
    @Test void publicBoundaryUsesNeutralTypes() {
        for (var method : LoadFlowAdapter.class.getMethods()) {
            assertTrue(method.getReturnType().getName().startsWith("eu.gridverse.data.cnm"));
            for (var type : method.getParameterTypes()) assertTrue(type.getName().startsWith("eu.gridverse.data.cnm"));
        }
    }
}
