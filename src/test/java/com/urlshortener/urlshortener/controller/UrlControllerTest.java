package com.urlshortener.urlshortener.controller;

import com.urlshortener.urlshortener.dto.ShortenRequest;
import com.urlshortener.urlshortener.dto.ShortenResponse;
import com.urlshortener.urlshortener.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link UrlController}.
 *
 * <p>Uses {@link WebMvcTest} to load only the web layer — no database involved.
 * {@link UrlService} is mocked with {@link MockitoBean} to isolate the controller.</p>
 */
@WebMvcTest(UrlController.class)
@DisplayName("UrlController")
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    // ─────────────────────────────────────────
    // POST /api/shorten
    // ─────────────────────────────────────────

    @Test
    @DisplayName("POST /api/shorten: should return 201 with shortUrl in body")
    void shorten_whenValidRequest_returns201() throws Exception {
        // Arrange
        ShortenRequest request = new ShortenRequest("https://www.google.com");
        ShortenResponse response = new ShortenResponse(
                "https://www.google.com",
                "http://localhost:8080/1",
                "1"
        );
        when(urlService.shorten(any(ShortenRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.com"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/1"))
                .andExpect(jsonPath("$.hash").value("1"));
    }

    @Test
    @DisplayName("POST /api/shorten: should return 400 when URL is blank")
    void shorten_whenUrlIsBlank_returns400() throws Exception {
        // Arrange
        ShortenRequest request = new ShortenRequest("");

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /api/shorten: should return 400 when URL has no http scheme")
    void shorten_whenUrlHasNoScheme_returns400() throws Exception {
        // Arrange
        ShortenRequest request = new ShortenRequest("www.google.com");

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("url: URL must start with http:// or https://"));
    }

    @Test
    @DisplayName("POST /api/shorten: should return 400 when body is missing")
    void shorten_whenBodyIsMissing_returns400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────
    // GET /{hash}
    // ─────────────────────────────────────────

    @Test
    @DisplayName("GET /{hash}: should return 302 with Location header")
    void redirect_whenHashExists_returns302() throws Exception {
        // Arrange
        when(urlService.resolveHash("1")).thenReturn("https://www.google.com");

        // Act & Assert
        mockMvc.perform(get("/1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.google.com"));
    }

    @Test
    @DisplayName("GET /{hash}: should return 404 when hash does not exist")
    void redirect_whenHashNotFound_returns404() throws Exception {
        // Arrange
        when(urlService.resolveHash("unknown"))
                .thenThrow(new IllegalArgumentException("No URL found for hash: unknown"));

        // Act & Assert
        mockMvc.perform(get("/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No URL found for hash: unknown"));
    }
}