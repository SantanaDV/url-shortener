package com.urlshortener.urlshortener.util;

/**
 * Utility class for encoding numeric IDs into Base62 strings.
 *
 * <p>Base62 uses 62 characters: digits (0-9), lowercase (a-z) and uppercase (A-Z).
 * With 6 characters it can represent 62^6 = 56 billion unique URLs.</p>
 */

public class Base62Encoder {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length(); // 62

    private Base62Encoder() {
        // Utility class, not meant to be instantiated
    }
    /**
     * Encodes a numeric database ID into a short Base62 string.
     *
     * <p>Example: {@code encode(12345)} returns {@code "3dV"}</p>
     *
     * @param id the auto-generated database ID (must be >= 0)
     * @return a short alphanumeric hash representing the given ID
     */
    public static String encode(long id) {
        if (id == 0) return String.valueOf(ALPHABET.charAt(0));

        StringBuilder result = new StringBuilder();

        while (id > 0) {
            int remainder = (int) (id % BASE);
            result.append(ALPHABET.charAt(remainder));
            id /= BASE;
        }
        // We reverse because digits are built from least significant to most significant
        return result.reverse().toString();
    }
}
