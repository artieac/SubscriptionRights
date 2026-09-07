package com.alwaysmoveforward.subscriptionrights.domainmodel;

import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;

import java.time.Instant;

/**
 * Aggregate root for a machine credential scoped to one Application. Never carries the raw
 * token -- only its SHA-256 hash (see security/apitoken/ApiTokenCrypto) and a short, non-sensitive
 * display prefix. The raw value is generated and shown to the admin exactly once, at creation
 * (see services/ApiTokenService), and cannot be recovered from this object or its storage
 * afterward.
 */
public class ApiToken {

    private final Long id;
    private final Long applicationId;
    private final String name;
    private final String tokenHash;
    private final String tokenPrefix;
    private final Instant createdAt;
    private Instant revokedAt;

    private ApiToken(Long id, Long applicationId, String name, String tokenHash, String tokenPrefix,
                      Instant createdAt, Instant revokedAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.name = name;
        this.tokenHash = tokenHash;
        this.tokenPrefix = tokenPrefix;
        this.createdAt = createdAt;
        this.revokedAt = revokedAt;
    }

    public static ApiToken create(Long applicationId, String name, String tokenHash, String tokenPrefix) {
        return new ApiToken(null, requireApplicationId(applicationId), requireName(name), tokenHash, tokenPrefix,
                Instant.now(), null);
    }

    /**
     * Reconstitutes an ApiToken from persisted state. Only mappers should call this.
     */
    public static ApiToken reconstitute(Long id, Long applicationId, String name, String tokenHash,
                                         String tokenPrefix, Instant createdAt, Instant revokedAt) {
        return new ApiToken(id, applicationId, name, tokenHash, tokenPrefix, createdAt, revokedAt);
    }

    public boolean isActive() {
        return revokedAt == null;
    }

    /** Idempotent -- revoking an already-revoked token is a no-op, not an error. */
    public void revoke() {
        if (revokedAt == null) {
            revokedAt = Instant.now();
        }
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("ApiToken name must not be blank");
        }
        return name;
    }

    private static Long requireApplicationId(Long applicationId) {
        if (applicationId == null) {
            throw new DomainException("ApiToken must belong to an Application");
        }
        return applicationId;
    }

    public Long getId() {
        return id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getName() {
        return name;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }
}
