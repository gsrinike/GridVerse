package eu.gridverse.data.cnm.security;

public record CnmViolation(String contingencyId, String elementId, String limitType, double value, double limit, double overloadPercent) {}
