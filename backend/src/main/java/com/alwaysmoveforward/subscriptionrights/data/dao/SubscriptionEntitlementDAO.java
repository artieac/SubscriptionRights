package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionEntitlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionEntitlementDAO extends JpaRepository<SubscriptionEntitlementEntity, Long> {

    List<SubscriptionEntitlementEntity> findByApplicationId(Long applicationId);
}
