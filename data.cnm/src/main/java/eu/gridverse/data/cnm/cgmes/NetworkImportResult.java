package eu.gridverse.data.cnm.cgmes;

import eu.gridverse.data.cnm.iidm.CnmNetworkSummary;
import java.util.List;
import java.util.UUID;

public record NetworkImportResult(UUID importId, boolean successful, String networkId, CnmNetworkSummary summary, List<String> warnings) {
    public NetworkImportResult { warnings = warnings == null ? List.of() : List.copyOf(warnings); }
}
