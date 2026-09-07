package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanGrant;

import java.time.Instant;

public class SubscriptionPlanGrantViewModel {

    private final Long id;
    private final Long applicationId;
    private final Long subscriptionPlanId;
    private final int subscriptionPlanVersion;
    private final Long subscriptionEntitlementId;
    private final int value;
    private final Instant createdAt;

    public SubscriptionPlanGrantViewModel(Long id, Long applicationId, Long subscriptionPlanId,
                                           int subscriptionPlanVersion, Long subscriptionEntitlementId, int value,
                                           Instant createdAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.subscriptionPlanId = subscriptionPlanId;
        this.subscriptionPlanVersion = subscriptionPlanVersion;
        this.subscriptionEntitlementId = subscriptionEntitlementId;
        this.value = value;
        this.createdAt = createdAt;
    }

    public static SubscriptionPlanGrantViewModel from(SubscriptionPlanGrant grant) {
        return new SubscriptionPlanGrantViewModel(grant.getId(), grant.getApplicationId(),
                grant.getSubscriptionPlanId(), grant.getSubscriptionPlanVersion(), grant.getSubscriptionEntitlementId(),
                grant.getValue(), grant.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public int getSubscriptionPlanVersion() {
        return subscriptionPlanVersion;
    }

    public Long getSubscriptionEntitlementId() {
        return subscriptionEntitlementId;
    }

    public int getValue() {
        return value;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
