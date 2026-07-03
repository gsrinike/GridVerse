package eu.gridverse.srv.optimization.services.config;
import eu.gridverse.solver.config.SolverConfiguration; import eu.gridverse.solver.optimization.OptimizationSolverAdapter; import eu.gridverse.solver.rao.PreventiveRaoProblemMapper; import eu.gridverse.solver.scip.ScipCommandSolverAdapter; import eu.gridverse.srv.optimization.services.service.OptimizationService; import eu.gridverse.srv.optimization.services.profile.PstProfilePackageParser;
import java.time.Duration; import org.springframework.beans.factory.annotation.Value; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration; import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration public class OptimizationConfiguration implements WebMvcConfigurer {
    @Bean SolverConfiguration solverConfiguration(@Value("${gridverse.solver.executable:scip}") String executable, @Value("${gridverse.solver.time-limit-seconds:300}") long seconds, @Value("${gridverse.solver.gap-tolerance:0.001}") double gap, @Value("${gridverse.solver.threads:1}") int threads, @Value("${gridverse.solver.fallback-enabled:false}") boolean fallback) { return new SolverConfiguration(executable, Duration.ofSeconds(seconds), gap, threads, fallback); }
    @Bean OptimizationSolverAdapter optimizationSolverAdapter(SolverConfiguration config) { return new ScipCommandSolverAdapter(config); }
    @Bean PreventiveRaoProblemMapper preventiveRaoProblemMapper() { return new PreventiveRaoProblemMapper(); }
    @Bean PstProfilePackageParser pstProfilePackageParser() { return new PstProfilePackageParser(); }
    @Bean OptimizationService optimizationService(OptimizationSolverAdapter solver, PreventiveRaoProblemMapper mapper, PstProfilePackageParser profiles) { return new OptimizationService(solver, mapper, profiles); }
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry){registry.addResourceHandler("/openapi/**").addResourceLocations("classpath:/openapi/");}
}
