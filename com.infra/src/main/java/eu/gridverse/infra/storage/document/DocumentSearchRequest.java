package eu.gridverse.infra.storage.document;

import java.util.Map;

public record DocumentSearchRequest(Map<String, Object> filters, int page, int size) {
    public DocumentSearchRequest {
        filters = filters == null ? Map.of() : Map.copyOf(filters);
        if (page < 0 || size < 1 || size > 500) throw new IllegalArgumentException("invalid page request");
    }
}
