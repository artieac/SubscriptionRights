package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.ApiToken;

import java.time.Instant;

/** Never carries the token hash, and never the raw token -- see IssuedApiTokenViewModel for the one-time exception. */
public class ApiTokenViewModel {

    private final Long id;
    private final Long applicationId;
    private final String name;
    private final String tokenPrefix;
    private final Instant createdAt;
    private final Instant revokedAt;

    public ApiTokenViewModel(Long id, Long applicationId, String name, String tokenPrefix, Instant createdAt,
                              Instant revokedAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.name = name;
        this.tokenPrefix = tokenPrefix;
        this.createdAt = createdAt;
        this.revokedAt = revokedAt;
    }

    public static ApiTokenViewModel from(ApiToken apiToken) {
        return new ApiTokenViewModel(apiToken.getId(), apiToken.getApplicationId(), apiToken.getName(),
                apiToken.getTokenPrefix(), apiToken.getCreatedAt(), apiToken.getRevokedAt());
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
}
