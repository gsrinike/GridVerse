package eu.gridverse.data.cnm.iidm;

public record CnmNetworkSummary(String networkId, int substations, int voltageLevels, int buses, int lines, int transformers, int generators, int loads) {}
