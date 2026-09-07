package com.alwaysmoveforward.subscriptionrights.domainmodel;

import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;

import java.time.Instant;

/**
 * Aggregate root for an application that owns subscription tiers, subscription
 * rights, and the grants linking them.
 */
public class Application {

    private final Long id;
    private String name;
    private String description;
    private final Instant createdAt;

    private Application(Long id, String name, String description, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Application create(String name, String description) {
        return new Application(null, requireName(name), description, Instant.now());
    }

    /**
     * Reconstitutes an Application from persisted state. Only mappers should call this.
     */
    public static Application reconstitute(Long id, String name, String description, Instant createdAt) {
        return new Application(id, name, description, createdAt);
    }

    public void rename(String newName) {
        this.name = requireName(newName);
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Application name must not be blank");
        }
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
