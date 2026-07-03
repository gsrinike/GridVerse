package eu.gridverse.data.cnm.rao;

import java.util.List;
import java.util.Map;

public record RaoTimeframeInput(String timeframe, List<PstCandidate> psts, List<MonitoredBranch> monitoredBranches, Map<String, Double> tsoMovementLimits) {
    public RaoTimeframeInput { psts = psts == null ? List.of() : List.copyOf(psts); monitoredBranches = monitoredBranches == null ? List.of() : List.copyOf(monitoredBranches); tsoMovementLimits = tsoMovementLimits == null ? Map.of() : Map.copyOf(tsoMovementLimits); }
}
