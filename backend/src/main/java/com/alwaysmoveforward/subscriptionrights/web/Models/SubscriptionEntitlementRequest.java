package com.alwaysmoveforward.subscriptionrights.web.Models;

import jakarta.validation.constraints.NotBlank;

public class SubscriptionEntitlementRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String displayName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
