package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.services.ApiTokenService;

import java.time.Instant;

/**
 * The only response shape that ever carries a raw token value, and only once: the moment a token
 * is created. It is never shown again after this response -- only ApiTokenViewModel's masked
 * tokenPrefix is available afterward.
 */
public class IssuedApiTokenViewModel {

    private final Long id;
    private final Long applicationId;
    private final String name;
    private final String tokenPrefix;
    private final Instant createdAt;
    private final Instant revokedAt;
    private final String rawToken;

    public IssuedApiTokenViewModel(Long id, Long applicationId, String name, String tokenPrefix, Instant createdAt,
                                    Instant revokedAt, String rawToken) {
        this.id = id;
        this.applicationId = applicationId;
        this.name = name;
        this.tokenPrefix = tokenPrefix;
        this.createdAt = createdAt;
        this.revokedAt = revokedAt;
        this.rawToken = rawToken;
    }

    public static IssuedApiTokenViewModel from(ApiTokenService.IssuedApiToken issued) {
        return new IssuedApiTokenViewModel(issued.apiToken().getId(), issued.apiToken().getApplicationId(),
                issued.apiToken().getName(), issued.apiToken().getTokenPrefix(), issued.apiToken().getCreatedAt(),
                issued.apiToken().getRevokedAt(), issued.rawToken());
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

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public String getRawToken() {
        return rawToken;
    }
}
