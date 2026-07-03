package eu.gridverse.utils.env;

import java.util.Map;

public final class EnvironmentResolver {
    private EnvironmentResolver() {}

    public static String resolve(Map<String, String> environment) {
        String systemValue = System.getProperty("env");
        if (systemValue != null && !systemValue.isBlank()) return systemValue.trim();
        String environmentValue = environment.get("ENV");
        return environmentValue == null || environmentValue.isBlank() ? "local" : environmentValue.trim();
    }
}
