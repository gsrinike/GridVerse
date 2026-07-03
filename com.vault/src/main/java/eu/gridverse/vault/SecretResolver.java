package eu.gridverse.vault;

import java.util.Optional;

public interface SecretResolver { Optional<String> resolve(String key); }
