package com.alwaysmoveforward.subscriptionrights.services;

/**
 * Plain input carrier for one item in a SubscriptionPlanSetRequest -- the controller maps
 * request item DTOs into these before calling SubscriptionPlanSetService, which is
 * responsible for turning them into validated SubscriptionPlanSetItem domain objects.
 */
public record SubscriptionPlanSetItemInput(Long subscriptionPlanId, int subscriptionPlanVersion, int tier) {
}
