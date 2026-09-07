package com.alwaysmoveforward.subscriptionrights.web.Models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class SubscriptionPlanSetItemRequest {

    @NotNull
    private Long subscriptionPlanId;

    @NotNull
    @Positive
    private Integer subscriptionPlanVersion;

    @NotNull
    @PositiveOrZero
    private Integer tier;

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(Long subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public Integer getSubscriptionPlanVersion() {
        return subscriptionPlanVersion;
    }

    public void setSubscriptionPlanVersion(Integer subscriptionPlanVersion) {
        this.subscriptionPlanVersion = subscriptionPlanVersion;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }
}
