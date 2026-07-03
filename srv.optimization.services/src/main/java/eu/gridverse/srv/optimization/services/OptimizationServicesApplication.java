package eu.gridverse.srv.optimization.services;
import org.springframework.boot.SpringApplication; import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication public class OptimizationServicesApplication { public static void main(String[] args) { System.setProperty("MODULE", "srv.optimization.services"); SpringApplication.run(OptimizationServicesApplication.class, args); } }
