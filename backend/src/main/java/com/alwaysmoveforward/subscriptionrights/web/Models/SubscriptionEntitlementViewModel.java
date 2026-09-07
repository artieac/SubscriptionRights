package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionEntitlement;

import java.time.Instant;

public class SubscriptionEntitlementViewModel {

    private final Long id;
    private final Long applicationId;
    private final String name;
    private final String displayName;
    private final Instant createdAt;
    private final Instant updatedAt;

    public SubscriptionEntitlementViewModel(Long id, Long applicationId, String name, String displayName,
                                             Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.name = name;
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SubscriptionEntitlementViewModel from(SubscriptionEntitlement entitlement) {
        return new SubscriptionEntitlementViewModel(entitlement.getId(), entitlement.getApplicationId(), entitlement.getName(),
                entitlement.getDisplayName(), entitlement.getCreatedAt(), entitlement.getUpdatedAt());
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
