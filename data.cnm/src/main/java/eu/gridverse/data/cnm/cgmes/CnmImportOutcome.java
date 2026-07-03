package eu.gridverse.data.cnm.cgmes;
import eu.gridverse.data.cnm.common.ImportStatus;
public record CnmImportOutcome(ImportStatus importStatus, NetworkImportResult network) {}
