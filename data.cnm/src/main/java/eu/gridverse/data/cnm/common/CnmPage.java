package eu.gridverse.data.cnm.common;

import java.util.List;

public record CnmPage<T>(List<T> items, long total, int page, int size) {
    public CnmPage { items = items == null ? List.of() : List.copyOf(items); }
}
