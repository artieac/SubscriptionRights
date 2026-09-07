package com.alwaysmoveforward.subscriptionrights.services;

import com.alwaysmoveforward.subscriptionrights.data.repositories.ApplicationRepository;
import com.alwaysmoveforward.subscriptionrights.data.repositories.SubscriptionPlanGrantRepository;
import com.alwaysmoveforward.subscriptionrights.data.repositories.SubscriptionPlanRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlan;
import com.alwaysmoveforward.subscriptionrights.exceptions.ConflictException;
import com.alwaysmoveforward.subscriptionrights.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ApplicationRepository applicationRepository;
    private final SubscriptionPlanGrantRepository subscriptionPlanGrantRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository subscriptionPlanRepository,
                                    ApplicationRepository applicationRepository,
                                    SubscriptionPlanGrantRepository subscriptionPlanGrantRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.applicationRepository = applicationRepository;
        this.subscriptionPlanGrantRepository = subscriptionPlanGrantRepository;
    }

    public List<SubscriptionPlan> listForApplication(Long applicationId) {
        requireApplication(applicationId);
        return subscriptionPlanRepository.findAllCurrentByApplicationId(applicationId);
    }

    public SubscriptionPlan getPlan(Long applicationId, Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findCurrentById(planId)
                .orElseThrow(() -> new NotFoundException("SubscriptionPlan " + planId + " not found"));
        requireBelongsToApplication(plan, applicationId);
        return plan;
    }

    /**
     * All versions of one plan, newest first -- lets callers (e.g. the SubscriptionPlanSet
     * item picker) choose a specific version rather than only ever seeing the current one.
     */
    public List<SubscriptionPlan> listVersions(Long applicationId, Long planId) {
        getPlan(applicationId, planId);
        return subscriptionPlanRepository.findAllVersions(planId);
    }

    /**
     * One specific (not necessarily current) version of a plan -- lets callers (e.g. the
     * Subscription Plan Grants page) view/work with a historical version, not just the latest.
     */
    public SubscriptionPlan getPlanVersion(Long applicationId, Long planId, int version) {
        SubscriptionPlan match = subscriptionPlanRepository.findAllVersions(planId).stream()
                .filter(plan -> plan.getVersion() == version)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("SubscriptionPlan " + planId + " version " + version + " not found"));
        requireBelongsToApplication(match, applicationId);
        return match;
    }

    @Transactional
    public SubscriptionPlan createPlan(Long applicationId, String name, String description) {
        requireApplication(applicationId);
        requireNameNotInUse(applicationId, name, null);
        return subscriptionPlanRepository.createNewPlan(applicationId, name, description);
    }

    @Transactional
    public SubscriptionPlan updatePlan(Long applicationId, Long planId, String name, String description) {
        SubscriptionPlan current = getPlan(applicationId, planId);
        requireNameNotInUse(applicationId, name, planId);
        SubscriptionPlan next = current.nextVersion(name, description);
        return subscriptionPlanRepository.saveNewVersion(next);
    }

    /**
     * MySQL has no filtered/partial unique index, so "unique Name per Application among
     * latest-version plans" (excludeId lets a rename compare against every OTHER plan,
     * not itself) is enforced here instead of at the database.
     */
    private void requireNameNotInUse(Long applicationId, String name, Long excludePlanId) {
        boolean collides = subscriptionPlanRepository.findAllCurrentByApplicationId(applicationId).stream()
                .anyMatch(plan -> !plan.getId().equals(excludePlanId) && plan.getName().equals(name));
        if (collides) {
            throw new ConflictException(
                    "Application " + applicationId + " already has a SubscriptionPlan named '" + name + "'");
        }
    }

    @Transactional
    public void deletePlan(Long applicationId, Long planId) {
        getPlan(applicationId, planId);
        if (!subscriptionPlanGrantRepository.findByApplicationIdAndSubscriptionPlanId(applicationId, planId).isEmpty()) {
            throw new ConflictException(
                    "Cannot delete SubscriptionPlan " + planId + " -- it still has grants referencing it");
        }
        subscriptionPlanRepository.deleteAllVersions(planId);
    }

    private void requireApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new NotFoundException("Application " + applicationId + " not found");
        }
    }

    private void requireBelongsToApplication(SubscriptionPlan plan, Long applicationId) {
        if (!plan.getApplicationId().equals(applicationId)) {
            throw new NotFoundException("SubscriptionPlan " + plan.getId() + " not found for application " + applicationId);
        }
    }
}
