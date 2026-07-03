package eu.gridverse.utils.rest;

import java.time.Instant;
import java.util.List;

public record ApiError(String code, String message, List<String> details, Instant timestamp) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, List.of(), Instant.now());
    }
}
