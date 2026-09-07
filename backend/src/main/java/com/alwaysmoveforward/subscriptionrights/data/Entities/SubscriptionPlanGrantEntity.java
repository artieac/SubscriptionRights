package com.alwaysmoveforward.subscriptionrights.data.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "SubscriptionPlanGrants")
public class SubscriptionPlanGrantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "ApplicationId", nullable = false)
    private Long applicationId;

    @Column(name = "SubscriptionPlanId", nullable = false)
    private Long subscriptionPlanId;

    @Column(name = "SubscriptionPlanVersion", nullable = false)
    private Integer subscriptionPlanVersion;

    @Column(name = "SubscriptionEntitlementId", nullable = false)
    private Long subscriptionEntitlementId;

    // Backtick-quoted: "VALUE" is a reserved SQL keyword (H2 rejects it outright unquoted;
    // MySQL 8 is inconsistent about it depending on context) -- the backticks tell Hibernate to
    // apply dialect-appropriate identifier quoting to every generated query referencing this
    // column, everywhere, rather than relying on it happening to work unquoted.
    @Column(name = "`Value`", nullable = false)
    private Integer value;

    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
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

    public Long getSubscriptionEntitlementId() {
        return subscriptionEntitlementId;
    }

    public void setSubscriptionEntitlementId(Long subscriptionEntitlementId) {
        this.subscriptionEntitlementId = subscriptionEntitlementId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
