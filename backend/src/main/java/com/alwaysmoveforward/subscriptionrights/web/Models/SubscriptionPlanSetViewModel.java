package com.alwaysmoveforward.subscriptionrights.web.Models;

import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSet;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class SubscriptionPlanSetViewModel {

    private final Long id;
    private final Long applicationId;
    private final String name;
    private final LocalDate effectiveStartDate;
    private final LocalDate effectiveEndDate;
    private final List<SubscriptionPlanSetItemViewModel> items;
    private final Instant createdAt;
    private final Instant updatedAt;

    public SubscriptionPlanSetViewModel(Long id, Long applicationId, String name, LocalDate effectiveStartDate,
                                         LocalDate effectiveEndDate, List<SubscriptionPlanSetItemViewModel> items,
                                         Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.name = name;
        this.effectiveStartDate = effectiveStartDate;
        this.effectiveEndDate = effectiveEndDate;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SubscriptionPlanSetViewModel from(SubscriptionPlanSet set) {
        List<SubscriptionPlanSetItemViewModel> items = set.getItems().stream()
                .map(SubscriptionPlanSetItemViewModel::from).toList();
        return new SubscriptionPlanSetViewModel(set.getId(), set.getApplicationId(), set.getName(),
                set.getEffectiveStartDate(), set.getEffectiveEndDate(), items, set.getCreatedAt(), set.getUpdatedAt());
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

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public List<SubscriptionPlanSetItemViewModel> getItems() {
        return items;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
