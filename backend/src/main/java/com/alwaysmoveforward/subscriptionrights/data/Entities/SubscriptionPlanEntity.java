package com.alwaysmoveforward.subscriptionrights.data.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "SubscriptionPlans")
public class SubscriptionPlanEntity {

    @EmbeddedId
    private SubscriptionPlanEntityId id;

    @Column(name = "ApplicationId", nullable = false)
    private Long applicationId;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    public SubscriptionPlanEntityId getId() {
        return id;
    }

    public void setId(SubscriptionPlanEntityId id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
