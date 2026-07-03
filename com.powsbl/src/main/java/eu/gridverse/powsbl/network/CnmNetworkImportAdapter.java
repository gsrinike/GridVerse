package eu.gridverse.powsbl.network;
import eu.gridverse.data.cnm.cgmes.NetworkImportRequest;
import eu.gridverse.data.cnm.cgmes.NetworkImportResult;
public interface CnmNetworkImportAdapter { NetworkImportResult importNetwork(NetworkImportRequest request); }
