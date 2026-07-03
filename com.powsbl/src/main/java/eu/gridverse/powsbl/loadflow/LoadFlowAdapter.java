package eu.gridverse.powsbl.loadflow;
import eu.gridverse.data.cnm.loadflow.LoadFlowRequest;
import eu.gridverse.data.cnm.loadflow.LoadFlowResult;
public interface LoadFlowAdapter { LoadFlowResult run(LoadFlowRequest request); }
