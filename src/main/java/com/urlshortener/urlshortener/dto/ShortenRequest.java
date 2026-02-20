package com.urlshortener.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for the URL shortening endpoint.
 *
 * <p>Contains the original URL provided by the client.
 * Validation annotations ensure the input is not empty and follows URL format.</p>
 *
 * @param url the original long URL to be shortened
 */
public record ShortenRequest(

        @NotBlank(message = "URL must not be blank")
        @Pattern(
                regexp = "^(https?://).+",
                message = "URL must start with http:// or https://"
        )
        String url
) {}