package eu.gridverse.infra.storage.document;

import java.util.List;
import java.util.Optional;

public interface DocumentRepositoryService {
    <T> T save(String index, String id, T document);
    <T> Optional<T> find(String index, String id, Class<T> type);
    <T> List<T> search(String index, DocumentSearchRequest request, Class<T> type);
}
