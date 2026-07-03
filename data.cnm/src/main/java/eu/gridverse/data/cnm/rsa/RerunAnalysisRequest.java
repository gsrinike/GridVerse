package eu.gridverse.data.cnm.rsa;
import eu.gridverse.data.cnm.security.CnmContingency;
import java.util.List;
public record RerunAnalysisRequest(String networkId, List<CnmContingency> contingencies) { public RerunAnalysisRequest { contingencies = contingencies == null ? List.of() : List.copyOf(contingencies); } }
