package eu.gridverse.srv.computation.services;
import eu.gridverse.powsbl.config.PowsyblAdapterConfiguration;
import org.springframework.boot.SpringApplication; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.context.annotation.Import;
@SpringBootApplication @Import(PowsyblAdapterConfiguration.class) public class ComputationServicesApplication {
    public static void main(String[] args) { System.setProperty("MODULE", "srv.computation.services"); SpringApplication.run(ComputationServicesApplication.class, args); }
}
