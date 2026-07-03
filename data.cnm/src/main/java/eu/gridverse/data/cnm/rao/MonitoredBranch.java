package eu.gridverse.data.cnm.rao;

import java.util.Map;

public record MonitoredBranch(String id, String label, double baseCurrentA, double currentLimitA, Map<String, Double> currentSensitivityByPst) {
    public MonitoredBranch { currentSensitivityByPst = currentSensitivityByPst == null ? Map.of() : Map.copyOf(currentSensitivityByPst); }
}
