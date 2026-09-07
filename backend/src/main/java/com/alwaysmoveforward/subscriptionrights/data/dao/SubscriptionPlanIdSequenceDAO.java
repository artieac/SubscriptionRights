package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanIdSequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanIdSequenceDAO extends JpaRepository<SubscriptionPlanIdSequenceEntity, Long> {
}
