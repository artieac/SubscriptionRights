package com.alwaysmoveforward.subscriptionrights.domainmodel;

import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;

import java.util.Objects;

/**
 * One SubscriptionPlan VERSION included in a SubscriptionPlanSet, tiered within that set.
 * A value object -- it has no identity of its own outside the SubscriptionPlanSet aggregate
 * that owns it, and carries no persistence id at the domain level.
 */
public class SubscriptionPlanSetItem {

    private final Long subscriptionPlanId;
    private final int subscriptionPlanVersion;
    private final int tier;

    private SubscriptionPlanSetItem(Long subscriptionPlanId, int subscriptionPlanVersion, int tier) {
        this.subscriptionPlanId = subscriptionPlanId;
        this.subscriptionPlanVersion = subscriptionPlanVersion;
        this.tier = tier;
    }

    public static SubscriptionPlanSetItem of(Long subscriptionPlanId, int subscriptionPlanVersion, int tier) {
        if (subscriptionPlanId == null) {
            throw new DomainException("A SubscriptionPlanSetItem requires a subscriptionPlanId");
        }
        if (subscriptionPlanVersion < 1) {
            throw new DomainException("A SubscriptionPlanSetItem requires a valid subscriptionPlanVersion");
        }
        if (tier < 0) {
            throw new DomainException("SubscriptionPlanSetItem tier must not be negative");
        }
        return new SubscriptionPlanSetItem(subscriptionPlanId, subscriptionPlanVersion, tier);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionPlanSetItem)) {
            return false;
        }
        SubscriptionPlanSetItem that = (SubscriptionPlanSetItem) o;
        return subscriptionPlanVersion == that.subscriptionPlanVersion && tier == that.tier
                && Objects.equals(subscriptionPlanId, that.subscriptionPlanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriptionPlanId, subscriptionPlanVersion, tier);
    }
}
