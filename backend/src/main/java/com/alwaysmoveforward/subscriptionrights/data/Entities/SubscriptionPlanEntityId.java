package com.alwaysmoveforward.subscriptionrights.data.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubscriptionPlanEntityId implements Serializable {

    @Column(name = "Id")
    private Long id;

    @Column(name = "Version")
    private Integer version;

    public SubscriptionPlanEntityId() {
    }

    public SubscriptionPlanEntityId(Long id, Integer version) {
        this.id = id;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionPlanEntityId)) {
            return false;
        }
        SubscriptionPlanEntityId that = (SubscriptionPlanEntityId) o;
        return Objects.equals(id, that.id) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }
}
