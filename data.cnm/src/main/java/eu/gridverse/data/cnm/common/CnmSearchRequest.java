package eu.gridverse.data.cnm.common;

import java.time.LocalDate;

public record CnmSearchRequest(ProfileFamily profileFamily, String tso, LocalDate businessDay, String businessTime, int page, int size) {
    public CnmSearchRequest { if (page < 0 || size < 1 || size > 500) throw new IllegalArgumentException("invalid page request"); }
}
