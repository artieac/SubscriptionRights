package com.alwaysmoveforward.subscriptionrights.web.Models;

import jakarta.validation.constraints.NotNull;

public class SubscriptionPlanGrantItemRequest {

    @NotNull
    private Long subscriptionEntitlementId;

    @NotNull
    private Integer value;

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
