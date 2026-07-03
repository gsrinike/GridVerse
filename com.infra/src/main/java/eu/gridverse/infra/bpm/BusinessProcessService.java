package eu.gridverse.infra.bpm;

import java.util.Map;

public interface BusinessProcessService {
    String start(String processKey, String businessKey, Map<String, Object> variables);
    void correlate(String messageName, String businessKey, Map<String, Object> variables);
}
