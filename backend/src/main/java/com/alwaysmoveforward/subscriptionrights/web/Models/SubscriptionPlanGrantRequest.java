package com.alwaysmoveforward.subscriptionrights.web.Models;

import jakarta.validation.constraints.NotNull;

public class SubscriptionPlanGrantRequest {

    @NotNull
    private Long subscriptionPlanId;

    @NotNull
    private Long subscriptionEntitlementId;

    @NotNull
    private Integer value;

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(Long subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public Long getSubscriptionEntitlementId() {
        return subscriptionEntitlementId;
    }

    public void setSubscriptionEntitlementId(Long subscriptionEntitlementId) {
        this.subscriptionEntitlementId = subscriptionEntitlementId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
