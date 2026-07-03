package eu.gridverse.infra.metadata;

import java.util.Optional;

public interface MetadataRepository<T, ID> {
    T save(T value);
    Optional<T> findById(ID id);
}
