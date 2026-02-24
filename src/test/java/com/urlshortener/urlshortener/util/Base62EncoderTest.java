package com.urlshortener.urlshortener.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Base62Encoder}.
 *
 * <p>These test verify the correctness of the Base62 encoding algorithm
 * * for known input/output pairs and edge cases.</p>
 */
@DisplayName("Base62Encoder")
public class Base62EncoderTest {

    @Test
    @DisplayName("encode(0)should return '0")
    void encode_whenIdIsZero_returnsZero() {
        // Arrange
        long id = 0L;

        // Act
        String result = Base62Encoder.encode(id);

        // Assert
        assertThat(result).isEqualTo("0");
    }

    @Test
    @DisplayName("encode(1) should return '1'")
    void encode_whenIdIsOne_returnsOne() {
        // Arrange
        long id = 1L;

        // Act
        String result = Base62Encoder.encode(id);

        // Assert
        assertThat(result).isEqualTo("1");
    }

    @Test
    @DisplayName("encode(61) should return 'Z' — last single character")
    void encode_whenIdIs61_returnsZ() {
        // Arrange
        long id = 61L;

        // Act
        String result = Base62Encoder.encode(id);

        // Assert
        assertThat(result).isEqualTo("Z");
    }

    @Test
    @DisplayName("encode(62) should return '10' — first two-character hash")
    void encode_whenIdIs62_returnsTwoCharacters() {
        // Arrange
        long id = 62L;

        // Act
        String result = Base62Encoder.encode(id);

        // Assert
        assertThat(result).isEqualTo("10");
    }

    @Test
    @DisplayName("encode(12345) should return '3dV'")
    void encode_whenIdIs12345_returnsExpectedHash() {
        // Arrange
        long id = 12345L;

        // Act
        String result = Base62Encoder.encode(id);

        // Assert
        assertThat(result).isEqualTo("3d7");
    }

    @Test
    @DisplayName("encode result should only contain Base62 characters")
    void encode_result_containsOnlyBase62Characters() {
        // Arrange
        long id = 999999L;

        // Act
        String result = Base62Encoder.encode(id);

        // Assert
        assertThat(result).matches("[0-9a-zA-Z]+");
    }

    @Test
    @DisplayName("encode result should never be null or empty")
    void encode_result_isNeverNullOrEmpty() {
        // Arrange
        long id = 42L;

        // Act
        String result = Base62Encoder.encode(id);

        // Assert
        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }
}




