package eu.gridverse.utils.env;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class EnvironmentResolverTest {
    @AfterEach void clear() { System.clearProperty("env"); }
    @Test void defaultsToLocal() { assertEquals("local", EnvironmentResolver.resolve(Map.of())); }
    @Test void systemPropertyWins() {
        System.setProperty("env", "test");
        assertEquals("test", EnvironmentResolver.resolve(Map.of("ENV", "prod")));
    }
}
