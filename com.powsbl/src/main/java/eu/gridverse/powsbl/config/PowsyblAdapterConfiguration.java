package eu.gridverse.powsbl.config;

import eu.gridverse.powsbl.loadflow.*; import eu.gridverse.powsbl.network.*; import eu.gridverse.powsbl.security.*; import eu.gridverse.powsbl.sensitivity.*;
import org.springframework.beans.factory.annotation.Value; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration public class PowsyblAdapterConfiguration {
    @Bean PowsyblNetworkRepository powsyblNetworkRepository(@Value("${powsybl.services.network-store-server.base-uri:}") String baseUri) { return new PowsyblNetworkRepository(baseUri); }
    @Bean CnmNetworkImportAdapter cnmNetworkImportAdapter(PowsyblNetworkRepository r) { return new PowsyblCnmNetworkImportAdapter(r); }
    @Bean LoadFlowAdapter loadFlowAdapter(PowsyblNetworkRepository r) { return new PowsyblLoadFlowAdapter(r); }
    @Bean SecurityAnalysisAdapter securityAnalysisAdapter(PowsyblNetworkRepository r) { return new PowsyblSecurityAnalysisAdapter(r); }
    @Bean SensitivityAnalysisAdapter sensitivityAnalysisAdapter(PowsyblNetworkRepository r) { return new PowsyblSensitivityAnalysisAdapter(r); }
    @Bean NetworkUpdateAdapter networkUpdateAdapter(PowsyblNetworkRepository r) { return new PowsyblNetworkUpdateAdapter(r); }
}
