package com.alwaysmoveforward.subscriptionrights.services;

/** One entitlement+value pair, as submitted to {@link SubscriptionPlanGrantService#replaceGrantsForPlan}. */
public record GrantValueInput(Long subscriptionEntitlementId, int value) {
}
