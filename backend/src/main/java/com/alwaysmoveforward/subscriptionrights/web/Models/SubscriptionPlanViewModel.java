package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlan;

import java.time.Instant;

public class SubscriptionPlanViewModel {

    private final Long id;
    private final int version;
    private final Long applicationId;
    private final String name;
    private final String description;
    private final Instant createdAt;

    public SubscriptionPlanViewModel(Long id, int version, Long applicationId, String name, String description,
                                      Instant createdAt) {
        this.id = id;
        this.version = version;
        this.applicationId = applicationId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static SubscriptionPlanViewModel from(SubscriptionPlan plan) {
        return new SubscriptionPlanViewModel(plan.getId(), plan.getVersion(), plan.getApplicationId(), plan.getName(),
                plan.getDescription(), plan.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public Long getApplicationId() {
        return applicationId;
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
