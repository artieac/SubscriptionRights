package com.alwaysmoveforward.subscriptionrights.web.Models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class SubscriptionPlanSetRequest {

    @NotBlank
    private String name;

    @NotNull
    private LocalDate effectiveStartDate;

    private LocalDate effectiveEndDate;

    @NotNull
    @Valid
    private List<SubscriptionPlanSetItemRequest> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(LocalDate effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public List<SubscriptionPlanSetItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SubscriptionPlanSetItemRequest> items) {
        this.items = items;
    }
}
