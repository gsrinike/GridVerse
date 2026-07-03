package eu.gridverse.powsbl.config;

import com.powsybl.iidm.network.Network;
import com.powsybl.network.store.client.NetworkStoreService;
import eu.gridverse.powsbl.PowerAnalysisException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PowsyblNetworkRepository {
    private final Map<String, Network> networks = new ConcurrentHashMap<>();
    private final NetworkStoreService remote;
    public PowsyblNetworkRepository() { this.remote = null; }
    public PowsyblNetworkRepository(String baseUri) { this.remote = baseUri == null || baseUri.isBlank() ? null : new NetworkStoreService(baseUri); }
    public Network importPath(String path) { Network network = remote == null ? Network.read(path) : remote.importNetwork(java.nio.file.Path.of(path)); put(identifier(network), network); return network; }
    public String identifier(Network network) { return remote == null ? network.getId() : remote.getNetworkUuid(network).toString(); }
    public void put(String id, Network network) { networks.put(id, network); }
    public Network get(String id) { Network network = networks.get(id); if (network == null && remote != null) { try { network = remote.getNetwork(java.util.UUID.fromString(id)); } catch (IllegalArgumentException exception) { throw new PowerAnalysisException("network id must be a UUID when network store is enabled: " + id, exception); } if (network != null) networks.put(id, network); } if (network == null) throw new PowerAnalysisException("network not loaded: " + id); return network; }
}
