package com.urlshortener.urlshortener.service;

import com.urlshortener.urlshortener.dto.ShortenRequest;
import com.urlshortener.urlshortener.dto.ShortenResponse;
import com.urlshortener.urlshortener.model.UrlMapping;
import com.urlshortener.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UrlServiceImpl}.
 *
 * <p>Uses Mockito to isolate the service from the database layer.
 * No Spring context is loaded — these tests are fast and self-contained.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UrlServiceImpl")
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    private UrlServiceImpl urlService;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        // We instantiate manually to inject the baseUrl string directly
        urlService = new UrlServiceImpl(urlRepository, BASE_URL);
    }

    // ─────────────────────────────────────────
    // shorten()
    // ─────────────────────────────────────────

    @Test
    @DisplayName("shorten: should return a valid ShortenResponse with shortUrl")
    void shorten_whenValidUrl_returnsShortUrl() {
        // Arrange
        ShortenRequest request = new ShortenRequest("https://www.google.com");

        UrlMapping savedMapping = new UrlMapping();
        savedMapping.setId(1L);
        savedMapping.setOriginalUrl("https://www.google.com");
        savedMapping.setHash("temp");

        // Mock: every call to save() returns our fake savedMapping
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(savedMapping);

        // Act
        ShortenResponse response = urlService.shorten(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.originalUrl()).isEqualTo("https://www.google.com");
        assertThat(response.shortUrl()).isEqualTo("http://localhost:8080/1");
        assertThat(response.hash()).isEqualTo("1");
    }

    @Test
    @DisplayName("shorten: hash should be Base62 encoding of the saved DB id")
    void shorten_whenUrlIsSaved_hashIsBase62EncodedId() {
        // Arrange
        ShortenRequest request = new ShortenRequest("https://www.example.com");

        UrlMapping savedMapping = new UrlMapping();
        savedMapping.setId(62L);
        savedMapping.setOriginalUrl("https://www.example.com");
        savedMapping.setHash("temp");

        when(urlRepository.save(any(UrlMapping.class))).thenReturn(savedMapping);

        // Act
        ShortenResponse response = urlService.shorten(request);

        // Assert — Base62(62) = "10"
        assertThat(response.hash()).isEqualTo("10");
        assertThat(response.shortUrl()).endsWith("/10");
    }

    @Test
    @DisplayName("shorten: should call save exactly twice (insert + update hash)")
    void shorten_shouldCallSaveTwice() {
        // Arrange
        ShortenRequest request = new ShortenRequest("https://www.google.com");

        UrlMapping savedMapping = new UrlMapping();
        savedMapping.setId(1L);
        savedMapping.setHash("temp");

        when(urlRepository.save(any(UrlMapping.class))).thenReturn(savedMapping);

        // Act
        urlService.shorten(request);

        // Assert — verify the double-save pattern
        verify(urlRepository, times(2)).save(any(UrlMapping.class));
    }

    // ─────────────────────────────────────────
    // resolveHash()
    // ─────────────────────────────────────────

    @Test
    @DisplayName("resolveHash: should return original URL when hash exists")
    void resolveHash_whenHashExists_returnsOriginalUrl() {
        // Arrange
        UrlMapping mapping = new UrlMapping();
        mapping.setHash("1");
        mapping.setOriginalUrl("https://www.google.com");

        when(urlRepository.findByHash("1")).thenReturn(Optional.of(mapping));

        // Act
        String result = urlService.resolveHash("1");

        // Assert
        assertThat(result).isEqualTo("https://www.google.com");
    }

    @Test
    @DisplayName("resolveHash: should throw IllegalArgumentException when hash not found")
    void resolveHash_whenHashNotFound_throwsException() {
        // Arrange
        when(urlRepository.findByHash("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> urlService.resolveHash("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No URL found for hash: unknown");
    }

    @Test
    @DisplayName("resolveHash: should never call save when resolving a hash")
    void resolveHash_shouldNeverCallSave() {
        // Arrange
        UrlMapping mapping = new UrlMapping();
        mapping.setHash("1");
        mapping.setOriginalUrl("https://www.google.com");

        when(urlRepository.findByHash("1")).thenReturn(Optional.of(mapping));

        // Act
        urlService.resolveHash("1");

        // Assert — resolving should never modify the database
        verify(urlRepository, never()).save(any());
    }
}