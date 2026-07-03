package eu.gridverse.data.cnm.common;

import java.util.UUID;

public record ChunkUploadCompleteRequest(UUID importId, String fileName, int chunkCount, long totalBytes, String sha256) {
    public ChunkUploadCompleteRequest {
        if (importId == null || fileName == null || fileName.isBlank()) throw new IllegalArgumentException("importId and fileName are required");
        if (chunkCount < 1 || totalBytes < 1) throw new IllegalArgumentException("upload must not be empty");
    }
}
