package com.alwaysmoveforward.subscriptionrights.services;

import com.alwaysmoveforward.subscriptionrights.data.repositories.ApplicationRepository;
import com.alwaysmoveforward.subscriptionrights.data.repositories.SubscriptionPlanRepository;
import com.alwaysmoveforward.subscriptionrights.data.repositories.SubscriptionPlanSetRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlan;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSet;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSetItem;
import com.alwaysmoveforward.subscriptionrights.exceptions.ConflictException;
import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;
import com.alwaysmoveforward.subscriptionrights.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Orchestrates SubscriptionPlanSet create/update. Two cross-aggregate rules live here rather
 * than on the domain model, since both require loading OTHER aggregates:
 *  - every item's (subscriptionPlanId, subscriptionPlanVersion) must exist and belong to this
 *    Application (checked against SubscriptionPlanRepository);
 *  - no two of an Application's sets may have overlapping active date ranges (checked against
 *    every other existing SubscriptionPlanSet for this Application, via SubscriptionPlanSet#overlaps).
 * Everything else (name/date-range validity, no duplicate plan/tier within one set) is
 * enforced by SubscriptionPlanSet itself.
 */
@Service
public class SubscriptionPlanSetService {

    private final SubscriptionPlanSetRepository subscriptionPlanSetRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ApplicationRepository applicationRepository;

    public SubscriptionPlanSetService(SubscriptionPlanSetRepository subscriptionPlanSetRepository,
                                       SubscriptionPlanRepository subscriptionPlanRepository,
                                       ApplicationRepository applicationRepository) {
        this.subscriptionPlanSetRepository = subscriptionPlanSetRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.applicationRepository = applicationRepository;
    }

    public List<SubscriptionPlanSet> listForApplication(Long applicationId) {
        requireApplication(applicationId);
        return subscriptionPlanSetRepository.findAllByApplicationId(applicationId);
    }

    public SubscriptionPlanSet getSet(Long applicationId, Long setId) {
        SubscriptionPlanSet set = subscriptionPlanSetRepository.findById(setId)
                .orElseThrow(() -> new NotFoundException("SubscriptionPlanSet " + setId + " not found"));
        requireBelongsToApplication(set, applicationId);
        return set;
    }

    public SubscriptionPlanSet getActiveForApplicationOnDate(Long applicationId, LocalDate date) {
        requireApplication(applicationId);
        return subscriptionPlanSetRepository.findActiveForApplicationOnDate(applicationId, date)
                .orElseThrow(() -> new NotFoundException(
                        "No SubscriptionPlanSet is active for application " + applicationId + " on " + date));
    }

    @Transactional
    public SubscriptionPlanSet create(Long applicationId, String name, LocalDate effectiveStartDate,
                                       LocalDate effectiveEndDate, List<SubscriptionPlanSetItemInput> itemInputs) {
        requireApplication(applicationId);
        List<SubscriptionPlanSetItem> items = resolveItems(applicationId, itemInputs);
        SubscriptionPlanSet candidate =
                SubscriptionPlanSet.create(applicationId, name, effectiveStartDate, effectiveEndDate, items);
        requireNoOverlap(candidate, subscriptionPlanSetRepository.findAllByApplicationId(applicationId));
        return subscriptionPlanSetRepository.save(candidate);
    }

    @Transactional
    public SubscriptionPlanSet update(Long applicationId, Long setId, String name, LocalDate effectiveStartDate,
                                       LocalDate effectiveEndDate, List<SubscriptionPlanSetItemInput> itemInputs) {
        SubscriptionPlanSet set = getSet(applicationId, setId);
        List<SubscriptionPlanSetItem> items = resolveItems(applicationId, itemInputs);

        set.rename(name);
        set.reschedule(effectiveStartDate, effectiveEndDate);
        set.updateItems(items);

        List<SubscriptionPlanSet> others = subscriptionPlanSetRepository.findAllByApplicationId(applicationId).stream()
                .filter(existing -> !existing.getId().equals(setId))
                .toList();
        requireNoOverlap(set, others);

        return subscriptionPlanSetRepository.save(set);
    }

    @Transactional
    public void delete(Long applicationId, Long setId) {
        getSet(applicationId, setId);
        subscriptionPlanSetRepository.delete(setId);
    }

    private List<SubscriptionPlanSetItem> resolveItems(Long applicationId, List<SubscriptionPlanSetItemInput> itemInputs) {
        return itemInputs.stream().map(input -> {
            SubscriptionPlan plan = subscriptionPlanRepository.findAllVersions(input.subscriptionPlanId()).stream()
                    .filter(version -> version.getVersion() == input.subscriptionPlanVersion())
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(
                            "SubscriptionPlan " + input.subscriptionPlanId() + " version "
                                    + input.subscriptionPlanVersion() + " not found"));
            if (!plan.getApplicationId().equals(applicationId)) {
                throw new DomainException(
                        "SubscriptionPlan " + plan.getId() + " does not belong to application " + applicationId);
            }
            return SubscriptionPlanSetItem.of(plan.getId(), plan.getVersion(), input.tier());
        }).toList();
    }

    private void requireNoOverlap(SubscriptionPlanSet candidate, List<SubscriptionPlanSet> existingSets) {
        for (SubscriptionPlanSet existing : existingSets) {
            if (candidate.overlaps(existing)) {
                throw new ConflictException(
                        "SubscriptionPlanSet's date range overlaps existing set '" + existing.getName()
                                + "' (id " + existing.getId() + ", " + existing.getEffectiveStartDate() + " - "
                                + existing.getEffectiveEndDate() + ")");
            }
        }
    }

    private void requireApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new NotFoundException("Application " + applicationId + " not found");
        }
    }

    private void requireBelongsToApplication(SubscriptionPlanSet set, Long applicationId) {
        if (!set.getApplicationId().equals(applicationId)) {
            throw new NotFoundException("SubscriptionPlanSet " + set.getId() + " not found for application " + applicationId);
        }
    }
}
