package eu.gridverse.powsbl.sensitivity;
import eu.gridverse.data.cnm.sensitivity.SensitivityAnalysisRequest;
import eu.gridverse.data.cnm.sensitivity.SensitivityAnalysisResult;
public interface SensitivityAnalysisAdapter { SensitivityAnalysisResult run(SensitivityAnalysisRequest request); }
