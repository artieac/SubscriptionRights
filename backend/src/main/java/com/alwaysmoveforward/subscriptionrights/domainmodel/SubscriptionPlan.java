package com.alwaysmoveforward.subscriptionrights.domainmodel;

import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;

import java.time.Instant;

/**
 * Aggregate root for a subscription plan belonging to one Application.
 *
 * Immutable and versioned: a plan's history is a sequence of SubscriptionPlan
 * instances that share the same id, one per version, none of them ever mutated
 * once created. Editing a plan means deriving its {@link #nextVersion} and
 * persisting it as a new row -- there is no "current" flag to maintain; a
 * plan's latest version is simply whichever row has the highest Version for
 * its id, which SubscriptionPlanRepository answers with a plain query. What is
 * actually active to an end user is never a property of SubscriptionPlan at
 * all -- that's entirely down to which SubscriptionPlanSet is active for a
 * given date, and which specific plan versions that set pins.
 */
public class SubscriptionPlan {

    private final Long id;
    private final int version;
    private final Long applicationId;
    private final String name;
    private final String description;
    private final Instant createdAt;

    private SubscriptionPlan(Long id, int version, Long applicationId, String name, String description,
                              Instant createdAt) {
        this.id = id;
        this.version = version;
        this.applicationId = applicationId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    /**
     * The first version of a brand-new plan. {@code id} must already be allocated by the
     * repository (from SubscriptionPlanIdSequence) before calling this -- domain models
     * don't generate persistence identifiers themselves.
     */
    public static SubscriptionPlan firstVersion(Long id, Long applicationId, String name, String description) {
        return new SubscriptionPlan(requireId(id), 1, requireApplicationId(applicationId), requireName(name),
                description, Instant.now());
    }

    /**
     * Derives the next version of this plan: same id, version + 1.
     * This instance itself is unchanged -- it's just an immutable snapshot of one version.
     */
    public SubscriptionPlan nextVersion(String newName, String newDescription) {
        return new SubscriptionPlan(id, version + 1, applicationId, requireName(newName), newDescription,
                Instant.now());
    }

    /**
     * Reconstitutes a SubscriptionPlan from persisted state. Only mappers should call this.
     */
    public static SubscriptionPlan reconstitute(Long id, int version, Long applicationId, String name,
                                                 String description, Instant createdAt) {
        return new SubscriptionPlan(id, version, applicationId, name, description, createdAt);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("SubscriptionPlan name must not be blank");
        }
        return name;
    }

    private static Long requireApplicationId(Long applicationId) {
        if (applicationId == null) {
            throw new DomainException("SubscriptionPlan must belong to an Application");
        }
        return applicationId;
    }

    private static Long requireId(Long id) {
        if (id == null) {
            throw new DomainException("SubscriptionPlan must have an id");
        }
        return id;
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
