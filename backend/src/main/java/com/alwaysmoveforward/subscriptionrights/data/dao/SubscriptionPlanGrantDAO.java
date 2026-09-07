package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanGrantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionPlanGrantDAO extends JpaRepository<SubscriptionPlanGrantEntity, Long> {

    List<SubscriptionPlanGrantEntity> findByApplicationId(Long applicationId);

    List<SubscriptionPlanGrantEntity> findByApplicationIdAndSubscriptionPlanId(Long applicationId, Long subscriptionPlanId);
}
