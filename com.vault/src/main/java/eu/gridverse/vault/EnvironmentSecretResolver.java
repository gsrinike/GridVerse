package eu.gridverse.vault;

import java.util.Map;
import java.util.Optional;

public final class EnvironmentSecretResolver implements SecretResolver {
    private final Map<String, String> environment;
    public EnvironmentSecretResolver(Map<String, String> environment) { this.environment = Map.copyOf(environment); }
    @Override public Optional<String> resolve(String key) { return Optional.ofNullable(environment.get(key)); }
}
