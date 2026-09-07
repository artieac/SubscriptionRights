package com.alwaysmoveforward.subscriptionrights.data.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Has no purpose other than minting new SubscriptionPlan ids: MySQL has no
 * CREATE SEQUENCE object, so a row is inserted here purely to get a fresh
 * AUTO_INCREMENT value back (via the generated id), and never deleted. See
 * SubscriptionPlanRepository.createNewPlan.
 */
@Entity
@Table(name = "SubscriptionPlanIdSequence")
public class SubscriptionPlanIdSequenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }
}
