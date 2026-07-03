package eu.gridverse.infra.storage.object;

import java.io.InputStream;
import java.util.Optional;

public interface ObjectStorageService {
    void put(String bucket, String key, InputStream content, long size, String contentType);
    Optional<InputStream> get(String bucket, String key);
    void delete(String bucket, String key);
}
