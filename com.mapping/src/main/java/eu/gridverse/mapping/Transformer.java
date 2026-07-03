package eu.gridverse.mapping;

@FunctionalInterface
public interface Transformer<S, T> { T transform(S source); }
