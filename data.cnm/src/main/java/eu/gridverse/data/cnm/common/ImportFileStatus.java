package eu.gridverse.data.cnm.common;

public record ImportFileStatus(String fileName, ImportFileState state, long bytes, String message) {
    public ImportFileStatus { if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("fileName is required"); }
}
