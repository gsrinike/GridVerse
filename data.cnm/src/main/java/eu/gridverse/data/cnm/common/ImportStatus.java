package eu.gridverse.data.cnm.common;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ImportStatus(UUID importId, ImportState state, List<ImportFileStatus> files, Instant updatedAt) {
    public ImportStatus { files = files == null ? List.of() : List.copyOf(files); }
}
