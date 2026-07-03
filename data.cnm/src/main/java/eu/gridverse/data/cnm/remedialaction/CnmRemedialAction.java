package eu.gridverse.data.cnm.remedialaction;

import java.util.Map;

public record CnmRemedialAction(String id, String name, String type, Map<String, Double> setpoints, double cost) {
    public CnmRemedialAction { setpoints = setpoints == null ? Map.of() : Map.copyOf(setpoints); }
}
