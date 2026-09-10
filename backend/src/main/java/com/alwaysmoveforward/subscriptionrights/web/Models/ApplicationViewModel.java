package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.Application;

import java.time.Instant;

public class ApplicationViewModel {

    private final Long id;
    private final String name;
    private final String externalId;
    private final String description;
    private final Instant createdAt;

    public ApplicationViewModel(Long id, String name, String externalId, String description, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static ApplicationViewModel from(Application application) {
        return new ApplicationViewModel(application.getId(), application.getName(), application.getExternalId(),
                application.getDescription(), application.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
