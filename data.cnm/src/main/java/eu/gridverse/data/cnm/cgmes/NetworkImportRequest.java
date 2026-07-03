package eu.gridverse.data.cnm.cgmes;

import java.util.List;
import java.util.UUID;

public record NetworkImportRequest(UUID importId, List<String> objectKeys) {
    public NetworkImportRequest { objectKeys = objectKeys == null ? List.of() : List.copyOf(objectKeys); }
}
