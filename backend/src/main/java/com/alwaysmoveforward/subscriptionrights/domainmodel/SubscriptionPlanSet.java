package com.alwaysmoveforward.subscriptionrights.domainmodel;

import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Aggregate root for the set of SubscriptionPlans (each pinned at a specific version) an
 * Application presents to a user as subscribable options during one date range.
 *
 * SubscriptionPlanSetItem has no repository of its own -- every read/write of a set's items
 * goes through this aggregate root, since "no duplicate plan in this set" and "no duplicate
 * tier in this set" can only be checked by seeing the whole item collection at once.
 */
public class SubscriptionPlanSet {

    private final Long id;
    private final Long applicationId;
    private String name;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private List<SubscriptionPlanSetItem> items;
    private final Instant createdAt;
    private Instant updatedAt;

    private SubscriptionPlanSet(Long id, Long applicationId, String name, LocalDate effectiveStartDate,
                                 LocalDate effectiveEndDate, List<SubscriptionPlanSetItem> items,
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

    public static SubscriptionPlanSet create(Long applicationId, String name, LocalDate effectiveStartDate,
                                              LocalDate effectiveEndDate, List<SubscriptionPlanSetItem> items) {
        Instant now = Instant.now();
        LocalDate start = requireStartDate(effectiveStartDate);
        LocalDate end = requireDateRange(start, effectiveEndDate);
        return new SubscriptionPlanSet(null, requireApplicationId(applicationId), requireName(name),
                start, end, requireValidItems(items, start, end), now, now);
    }

    /**
     * Reconstitutes a SubscriptionPlanSet from persisted state. Only the repository/mapper
     * should call this -- it does not re-validate, since persisted state is trusted.
     */
    public static SubscriptionPlanSet reconstitute(Long id, Long applicationId, String name,
                                                    LocalDate effectiveStartDate, LocalDate effectiveEndDate,
                                                    List<SubscriptionPlanSetItem> items,
                                                    Instant createdAt, Instant updatedAt) {
        return new SubscriptionPlanSet(id, applicationId, name, effectiveStartDate, effectiveEndDate,
                List.copyOf(items), createdAt, updatedAt);
    }

    public void rename(String newName) {
        this.name = requireName(newName);
        this.updatedAt = Instant.now();
    }

    public void reschedule(LocalDate newStart, LocalDate newEnd) {
        this.effectiveStartDate = requireStartDate(newStart);
        this.effectiveEndDate = requireDateRange(newStart, newEnd);
        this.updatedAt = Instant.now();
    }

    public void updateItems(List<SubscriptionPlanSetItem> newItems) {
        this.items = requireValidItems(newItems, effectiveStartDate, effectiveEndDate);
        this.updatedAt = Instant.now();
    }

    /**
     * True if this set's active date range intersects {@code other}'s. Both sets are assumed
     * to belong to the same Application -- callers (SubscriptionPlanSetService) are responsible
     * for only comparing sets within one application. A null end date is treated as unbounded.
     */
    public boolean overlaps(SubscriptionPlanSet other) {
        boolean startsBeforeOtherEnds = other.effectiveEndDate == null
                || !this.effectiveStartDate.isAfter(other.effectiveEndDate);
        boolean otherStartsBeforeThisEnds = this.effectiveEndDate == null
                || !other.effectiveStartDate.isAfter(this.effectiveEndDate);
        return startsBeforeOtherEnds && otherStartsBeforeThisEnds;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("SubscriptionPlanSet name must not be blank");
        }
        return name;
    }

    private static Long requireApplicationId(Long applicationId) {
        if (applicationId == null) {
            throw new DomainException("SubscriptionPlanSet must belong to an Application");
        }
        return applicationId;
    }

    private static LocalDate requireStartDate(LocalDate effectiveStartDate) {
        if (effectiveStartDate == null) {
            throw new DomainException("SubscriptionPlanSet requires an effectiveStartDate");
        }
        return effectiveStartDate;
    }

    private static LocalDate requireDateRange(LocalDate start, LocalDate end) {
        if (end != null && end.isBefore(start)) {
            throw new DomainException("SubscriptionPlanSet effectiveEndDate must not be before effectiveStartDate");
        }
        return end;
    }

    private static List<SubscriptionPlanSetItem> requireValidItems(List<SubscriptionPlanSetItem> items,
                                                                     LocalDate start, LocalDate end) {
        requireDateRange(start, end);
        if (items == null) {
            throw new DomainException("SubscriptionPlanSet items must not be null");
        }
        Set<Long> seenPlanIds = new HashSet<>();
        Set<Integer> seenTiers = new HashSet<>();
        for (SubscriptionPlanSetItem item : items) {
            if (!seenPlanIds.add(item.getSubscriptionPlanId())) {
                throw new DomainException(
                        "SubscriptionPlanSet cannot include plan " + item.getSubscriptionPlanId() + " more than once");
            }
            if (!seenTiers.add(item.getTier())) {
                throw new DomainException(
                        "SubscriptionPlanSet cannot assign tier " + item.getTier() + " to more than one plan");
            }
        }
        return new ArrayList<>(items);
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

    public List<SubscriptionPlanSetItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
