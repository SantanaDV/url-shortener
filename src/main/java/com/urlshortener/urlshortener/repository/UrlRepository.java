package com.urlshortener.urlshortener.repository;

import com.urlshortener.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for {@link UrlMapping} persistence operations.
 *
 * <p>Extends {@link JpaRepository} to inherit standard CRUD operations
 * (save, findById, delete, etc.) without writing a single line of SQL.</p>
 */
@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

    /**
     * Finds a URL mapping by its short hash.
     *
     * @param hash the Base62 encoded hash (e.g., "3dV")
     * @return an {@link Optional} containing the mapping if found, or empty if not
     */
    Optional<UrlMapping> findByHash(String hash);
}
