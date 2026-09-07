package com.alwaysmoveforward.subscriptionrights.security.apitoken;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Generates and hashes API tokens. SHA-256, not a slow password hash (bcrypt/Argon2/etc) --
 * unlike a human-chosen password, the token itself is 256 bits of random entropy, so there is
 * nothing for a slow hash to protect against; SHA-256 is fast and sufficient. The raw token is
 * generated once, handed back to the caller, and never stored -- only its hash is persisted (see
 * ApiTokenRepository / the ApiTokens table).
 */
@Component
public class ApiTokenCrypto {

    private static final String TOKEN_PREFIX = "srt_";
    private static final int RAW_ENTROPY_BYTES = 32;
    private static final int DISPLAY_PREFIX_LENGTH = 12;

    private final SecureRandom secureRandom = new SecureRandom();

    public record GeneratedToken(String rawToken, String tokenHash, String tokenPrefix) {
    }

    public GeneratedToken generate() {
        byte[] entropy = new byte[RAW_ENTROPY_BYTES];
        secureRandom.nextBytes(entropy);
        String rawToken = TOKEN_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(entropy);
        String tokenHash = hash(rawToken);
        String tokenPrefix = rawToken.substring(0, Math.min(DISPLAY_PREFIX_LENGTH, rawToken.length()));
        return new GeneratedToken(rawToken, tokenHash, tokenPrefix);
    }

    /** SHA-256 digest of {@code rawToken}, hex-encoded (lowercase, 64 chars). */
    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
