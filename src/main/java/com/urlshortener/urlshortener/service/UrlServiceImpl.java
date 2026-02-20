package com.urlshortener.urlshortener.service;

import com.urlshortener.urlshortener.dto.ShortenRequest;
import com.urlshortener.urlshortener.dto.ShortenResponse;
import com.urlshortener.urlshortener.model.UrlMapping;
import com.urlshortener.urlshortener.repository.UrlRepository;
import com.urlshortener.urlshortener.util.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link UrlService}.
 *
 * <p>Handles the core business logic: generating a unique Base62 hash
 * from the database ID and resolving hashes back to their original URLs.</p>
 */
@Service
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Flow:
     * <ol>
     *   <li>Save the original URL with a temporary placeholder hash</li>
     *   <li>Use the generated DB ID to produce a unique Base62 hash</li>
     *   <li>Update the record with the real hash and return the response</li>
     * </ol>
     * </p>
     */
    @Override
    @Transactional
    public ShortenResponse shorten(ShortenRequest request) {
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(request.url());
        urlMapping.setHash("placeholder");

        // First save to get the auto-generated ID from the database
        UrlMapping saved = urlRepository.save(urlMapping);

        // Use that ID to generate a guaranteed unique Base62 hash
        String hash = Base62Encoder.encode(saved.getId());
        saved.setHash(hash);

        // Save again with the real hash
        urlRepository.save(saved);

        String shortUrl = baseUrl + "/" + hash;
        return new ShortenResponse(request.url(), shortUrl, hash);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if no URL is found for the given hash
     */
    @Override
    public String resolveHash(String hash) {
        return urlRepository.findByHash(hash)
                .map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No URL found for hash: " + hash
                ));
    }
}