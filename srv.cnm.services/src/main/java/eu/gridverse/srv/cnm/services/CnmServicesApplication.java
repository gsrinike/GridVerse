package eu.gridverse.srv.cnm.services;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import eu.gridverse.powsbl.config.PowsyblAdapterConfiguration; import eu.gridverse.infra.config.InfrastructureAdapterConfiguration; import org.springframework.context.annotation.Import;
@SpringBootApplication @Import({PowsyblAdapterConfiguration.class,InfrastructureAdapterConfiguration.class}) public class CnmServicesApplication {
    public static void main(String[] args) { System.setProperty("MODULE", "srv.cnm.services"); SpringApplication.run(CnmServicesApplication.class, args); }
}
