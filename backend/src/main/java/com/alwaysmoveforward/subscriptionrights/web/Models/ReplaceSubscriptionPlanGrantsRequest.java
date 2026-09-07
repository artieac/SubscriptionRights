package com.alwaysmoveforward.subscriptionrights.web.Models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * The whole desired set of entitlement grants (and their values) for a plan going forward --
 * see SubscriptionPlanController#replaceGrants. Anything not listed here is not granted in the
 * new plan version this produces.
 */
public class ReplaceSubscriptionPlanGrantsRequest {

    @NotNull
    private List<@Valid SubscriptionPlanGrantItemRequest> items;

    /** true (the default a client should send): bump to a new plan version. false: update targetVersion's (or the current version's, if null) grants in place. */
    private boolean createNewVersion = true;

    /** Only meaningful when createNewVersion is false -- which existing version to update in place. Null means the current version. */
    private Integer targetVersion;

    public List<SubscriptionPlanGrantItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SubscriptionPlanGrantItemRequest> items) {
        this.items = items;
    }

    public boolean isCreateNewVersion() {
        return createNewVersion;
    }

    public void setCreateNewVersion(boolean createNewVersion) {
        this.createNewVersion = createNewVersion;
    }

    public Integer getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(Integer targetVersion) {
        this.targetVersion = targetVersion;
    }
}
