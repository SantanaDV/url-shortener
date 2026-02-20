package com.urlshortener.urlshortener.controller;

import com.urlshortener.urlshortener.dto.ShortenRequest;
import com.urlshortener.urlshortener.dto.ShortenResponse;
import com.urlshortener.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for URL shortening and redirection endpoints.
 *
 * <p>Exposes two endpoints:
 * <ul>
 *   <li>{@code POST /api/shorten} — receives a long URL and returns a short one</li>
 *   <li>{@code GET /{hash}} — resolves the hash and redirects to the original URL</li>
 * </ul>
 * </p>
 */
@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Shortens a given URL.
     *
     * @param request the request body containing the original URL
     * @return HTTP 201 Created with the {@link ShortenResponse} body
     */
    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        ShortenResponse response = urlService.shorten(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Redirects a short hash to its original URL.
     *
     * @param hash the Base62 hash extracted from the short URL
     * @return HTTP 302 Found with a Location header pointing to the original URL
     */
    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String originalUrl = urlService.resolveHash(hash);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, originalUrl);

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }
}