package com.urlshortener.urlshortener.service;


import com.urlshortener.urlshortener.dto.ShortenRequest;
import com.urlshortener.urlshortener.dto.ShortenResponse;

/**
 * Service interface for URL shortening operations.
 *
 * <p>Defining the contract as an interface allows us to swap implementations
 * (e.g., for testing with mocks) without touching the controller layer.</p>
 */
public interface UrlService {

    /**
     * Shortens a given URL and persists the mapping in the database.
     *
     * @param request the DTO containing the original URL
     * @return a {@link ShortenResponse} with the generated short URL and hash
     */
    ShortenResponse shorten(ShortenRequest request);

    /**
     * Resolves a Base62 hash back to its original URL.
     *
     * @param hash the Base62 encoded hash (e.g., "3dV")
     * @return the original long URL associated with the given hash
     * @throws RuntimeException if the hash is not found in the database
     */
    String resolveHash(String hash);
}