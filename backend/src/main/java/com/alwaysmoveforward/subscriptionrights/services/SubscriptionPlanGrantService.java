package com.alwaysmoveforward.subscriptionrights.services;

import com.alwaysmoveforward.subscriptionrights.data.repositories.SubscriptionPlanGrantRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionEntitlement;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlan;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanGrant;
import com.alwaysmoveforward.subscriptionrights.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Orchestrates granting a SubscriptionEntitlement to a SubscriptionPlan. The invariant
 * that both must belong to the same Application is enforced by the
 * {@link SubscriptionPlanGrant#grant} domain factory -- this service's job is just to
 * load the two aggregates (via their own services, so application-scoping is checked
 * the same way it is everywhere else) and persist the result.
 */
@Service
public class SubscriptionPlanGrantService {

    private final SubscriptionPlanGrantRepository subscriptionPlanGrantRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionEntitlementService subscriptionEntitlementService;

    public SubscriptionPlanGrantService(SubscriptionPlanGrantRepository subscriptionPlanGrantRepository,
                                         SubscriptionPlanService subscriptionPlanService,
                                         SubscriptionEntitlementService subscriptionEntitlementService) {
        this.subscriptionPlanGrantRepository = subscriptionPlanGrantRepository;
        this.subscriptionPlanService = subscriptionPlanService;
        this.subscriptionEntitlementService = subscriptionEntitlementService;
    }

    public List<SubscriptionPlanGrant> listForApplication(Long applicationId, Long subscriptionPlanId) {
        if (subscriptionPlanId != null) {
            return subscriptionPlanGrantRepository.findByApplicationIdAndSubscriptionPlanId(applicationId, subscriptionPlanId);
        }
        return subscriptionPlanGrantRepository.findByApplicationId(applicationId);
    }

    /**
     * The grants pinned to one specific version of a plan, rather than every version's.
     */
    public List<SubscriptionPlanGrant> listForPlanVersion(Long applicationId, Long subscriptionPlanId, int version) {
        return listForApplication(applicationId, subscriptionPlanId).stream()
                .filter(grant -> grant.getSubscriptionPlanVersion() == version)
                .toList();
    }

    public SubscriptionPlanGrant getGrant(Long applicationId, Long grantId) {
        SubscriptionPlanGrant grant = subscriptionPlanGrantRepository.findById(grantId)
                .orElseThrow(() -> new NotFoundException("SubscriptionPlanGrant " + grantId + " not found"));
        if (!grant.getApplicationId().equals(applicationId)) {
            throw new NotFoundException("SubscriptionPlanGrant " + grantId + " not found for application " + applicationId);
        }
        return grant;
    }

    @Transactional
    public SubscriptionPlanGrant createGrant(Long applicationId, Long subscriptionPlanId, Long subscriptionEntitlementId,
                                              int value) {
        SubscriptionPlan plan = subscriptionPlanService.getPlan(applicationId, subscriptionPlanId);
        SubscriptionEntitlement entitlement = subscriptionEntitlementService.getEntitlement(applicationId, subscriptionEntitlementId);
        SubscriptionPlanGrant grant = SubscriptionPlanGrant.grant(plan, entitlement, value);
        return subscriptionPlanGrantRepository.save(grant);
    }

    @Transactional
    public void deleteGrant(Long applicationId, Long grantId) {
        getGrant(applicationId, grantId);
        subscriptionPlanGrantRepository.deleteById(grantId);
    }

    /**
     * Replaces the whole set of entitlement grants for a plan, as one unit.
     *
     * <p>When {@code createNewVersion} is true (the normal case): bumps the plan to a new version
     * (same name/description, purely to carry this new grant configuration -- grants pin to an
     * exact plan version, so changing what's granted means a new version), then creates one grant
     * per {@code items} entry pinned to that new version. Grants on the plan's earlier versions
     * are untouched -- they remain exactly what they always were, part of that version's
     * immutable history.
     *
     * <p>When {@code createNewVersion} is false: no new version is created -- {@code targetVersion}
     * (or the current version, if null) has its existing grants deleted and replaced with fresh
     * ones matching {@code items}. This intentionally breaks the "a grant pins immutable history"
     * guarantee for whoever opts into it; it exists for iterating on a plan's entitlements --
     * including a historical version's, not just the current one -- without accumulating a
     * version per tweak.
     *
     * <p>Either way, any entitlement not present in {@code items} ends up not granted on the
     * resulting version.
     */
    @Transactional
    public SubscriptionPlan replaceGrantsForPlan(Long applicationId, Long subscriptionPlanId, List<GrantValueInput> items,
                                                  boolean createNewVersion, Integer targetVersion) {
        SubscriptionPlan currentPlan = subscriptionPlanService.getPlan(applicationId, subscriptionPlanId);
        SubscriptionPlan targetPlan;
        if (createNewVersion) {
            targetPlan = subscriptionPlanService.updatePlan(applicationId, subscriptionPlanId, currentPlan.getName(), currentPlan.getDescription());
        } else if (targetVersion != null && targetVersion != currentPlan.getVersion()) {
            targetPlan = subscriptionPlanService.getPlanVersion(applicationId, subscriptionPlanId, targetVersion);
        } else {
            targetPlan = currentPlan;
        }

        // A brand-new version has no grants yet, so this is a no-op in the createNewVersion=true
        // case; when updating the current version in place, this clears it before rebuilding it.
        // The flush is required: Hibernate's default flush ordering runs inserts before deletes
        // regardless of code order, so without forcing these deletes to land first, re-inserting
        // an unchanged (plan, version, entitlement) pair trips the unique constraint.
        subscriptionPlanGrantRepository.findByApplicationIdAndSubscriptionPlanId(applicationId, subscriptionPlanId).stream()
                .filter(grant -> grant.getSubscriptionPlanVersion() == targetPlan.getVersion())
                .forEach(grant -> subscriptionPlanGrantRepository.deleteById(grant.getId()));
        subscriptionPlanGrantRepository.flush();

        for (GrantValueInput item : items) {
            SubscriptionEntitlement entitlement = subscriptionEntitlementService.getEntitlement(applicationId,
                    item.subscriptionEntitlementId());
            SubscriptionPlanGrant grant = SubscriptionPlanGrant.grant(targetPlan, entitlement, item.value());
            subscriptionPlanGrantRepository.save(grant);
        }

        return targetPlan;
    }
}
