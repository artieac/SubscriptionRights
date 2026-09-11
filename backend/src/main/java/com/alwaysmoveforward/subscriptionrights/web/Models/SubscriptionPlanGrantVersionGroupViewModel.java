package com.alwaysmoveforward.subscriptionrights.web.Models;

import java.util.List;

/**
 * One plan version's grants, as an element of the version-grouped shape returned by
 * {@code GET .../subscription-plans/{id}/grants}.
 */
public class SubscriptionPlanGrantVersionGroupViewModel {

    private final int version;
    private final List<SubscriptionPlanGrantViewModel> grants;

    public SubscriptionPlanGrantVersionGroupViewModel(int version, List<SubscriptionPlanGrantViewModel> grants) {
        this.version = version;
        this.grants = grants;
    }

    public int getVersion() {
        return version;
    }

    public List<SubscriptionPlanGrantViewModel> getGrants() {
        return grants;
    }
}
