package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSetItem;

public class SubscriptionPlanSetItemViewModel {

    private final Long subscriptionPlanId;
    private final int subscriptionPlanVersion;
    private final int tier;

    public SubscriptionPlanSetItemViewModel(Long subscriptionPlanId, int subscriptionPlanVersion, int tier) {
        this.subscriptionPlanId = subscriptionPlanId;
        this.subscriptionPlanVersion = subscriptionPlanVersion;
        this.tier = tier;
    }

    public static SubscriptionPlanSetItemViewModel from(SubscriptionPlanSetItem item) {
        return new SubscriptionPlanSetItemViewModel(item.getSubscriptionPlanId(), item.getSubscriptionPlanVersion(),
                item.getTier());
    }

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public int getSubscriptionPlanVersion() {
        return subscriptionPlanVersion;
    }

    public int getTier() {
        return tier;
    }
}
