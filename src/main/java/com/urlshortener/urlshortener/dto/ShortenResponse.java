package com.urlshortener.urlshortener.dto;

/**
 * Response DTO returned after successfully shortening a URL.
 *
 * @param originalUrl the original long URL provided by the client
 * @param shortUrl    the generated short URL (e.g., "http://localhost:8080/3dV")
 * @param hash        the Base62 hash used as the short URL identifier
 */
public record ShortenResponse(
        String originalUrl,
        String shortUrl,
        String hash
) {}