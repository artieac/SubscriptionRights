package com.alwaysmoveforward.subscriptionrights.domainmodel;

import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;

import java.time.Instant;

/**
 * Aggregate root for a named entitlement (permission/right) that belongs to one Application.
 */
public class SubscriptionEntitlement {

    private final Long id;
    private final Long applicationId;
    private String name;
    private String displayName;
    private final Instant createdAt;
    private Instant updatedAt;

    private SubscriptionEntitlement(Long id, Long applicationId, String name, String displayName,
                                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.name = name;
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SubscriptionEntitlement create(Long applicationId, String name, String displayName) {
        Instant now = Instant.now();
        return new SubscriptionEntitlement(null, requireApplicationId(applicationId), requireName(name),
                requireDisplayName(displayName), now, now);
    }

    /**
     * Reconstitutes a SubscriptionEntitlement from persisted state. Only mappers should call this.
     */
    public static SubscriptionEntitlement reconstitute(Long id, Long applicationId, String name, String displayName,
                                                         Instant createdAt, Instant updatedAt) {
        return new SubscriptionEntitlement(id, applicationId, name, displayName, createdAt, updatedAt);
    }

    public void rename(String newName) {
        this.name = requireName(newName);
        this.updatedAt = Instant.now();
    }

    public void updateDisplayName(String newDisplayName) {
        this.displayName = requireDisplayName(newDisplayName);
        this.updatedAt = Instant.now();
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("SubscriptionEntitlement name must not be blank");
        }
        return name;
    }

    private static String requireDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new DomainException("SubscriptionEntitlement displayName must not be blank");
        }
        return displayName;
    }

    private static Long requireApplicationId(Long applicationId) {
        if (applicationId == null) {
            throw new DomainException("SubscriptionEntitlement must belong to an Application");
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

    public String getDisplayName() {
        return displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
