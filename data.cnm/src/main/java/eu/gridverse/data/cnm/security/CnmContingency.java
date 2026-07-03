package eu.gridverse.data.cnm.security;

import java.util.List;

public record CnmContingency(String id, String name, List<String> elementIds) {
    public CnmContingency { elementIds = elementIds == null ? List.of() : List.copyOf(elementIds); }
}
