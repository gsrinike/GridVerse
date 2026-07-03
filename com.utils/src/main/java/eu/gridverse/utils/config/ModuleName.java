package eu.gridverse.utils.config;

public record ModuleName(String value) {
    public ModuleName {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("module name is required");
    }
}
