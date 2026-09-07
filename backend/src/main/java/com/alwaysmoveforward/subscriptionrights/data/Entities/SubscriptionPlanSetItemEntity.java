package com.alwaysmoveforward.subscriptionrights.data.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * Owned entirely by the SubscriptionPlanSet aggregate -- only SubscriptionPlanSetRepository
 * (and its mapper) may reference this entity or its DAO. There is no
 * SubscriptionPlanSetItemRepository; items are never read or written except as part of
 * loading/saving the whole SubscriptionPlanSet they belong to.
 *
 * The unique constraints mirror database/schema.sql exactly (also declared here, not just
 * there, so a ddl-auto=create-drop test schema -- e.g. SubscriptionPlanSetRepositoryTest's --
 * enforces the same invariants a real SQL Server database would).
 */
@Entity
@Table(name = "SubscriptionPlanSetItems", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_SubscriptionPlanSetItems_Set_Plan",
                columnNames = {"SubscriptionPlanSetId", "SubscriptionPlanId"}),
        @UniqueConstraint(name = "UQ_SubscriptionPlanSetItems_Set_Tier",
                columnNames = {"SubscriptionPlanSetId", "Tier"})
})
public class SubscriptionPlanSetItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "SubscriptionPlanSetId", nullable = false)
    private Long subscriptionPlanSetId;

    @Column(name = "SubscriptionPlanId", nullable = false)
    private Long subscriptionPlanId;

    @Column(name = "SubscriptionPlanVersion", nullable = false)
    private Integer subscriptionPlanVersion;

    @Column(name = "Tier", nullable = false)
    private Integer tier;

    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriptionPlanSetId() {
        return subscriptionPlanSetId;
    }

    public void setSubscriptionPlanSetId(Long subscriptionPlanSetId) {
        this.subscriptionPlanSetId = subscriptionPlanSetId;
    }

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(Long subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public Integer getSubscriptionPlanVersion() {
        return subscriptionPlanVersion;
    }

    public void setSubscriptionPlanVersion(Integer subscriptionPlanVersion) {
        this.subscriptionPlanVersion = subscriptionPlanVersion;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
