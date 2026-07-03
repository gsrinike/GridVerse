package eu.gridverse.powsbl.network;
import eu.gridverse.data.cnm.remedialaction.ApplyRemedialActionsRequest;
import eu.gridverse.data.cnm.remedialaction.ApplyRemedialActionsResult;
public interface NetworkUpdateAdapter { ApplyRemedialActionsResult apply(ApplyRemedialActionsRequest request); }
