package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanSetItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Internal to the data layer -- used only by SubscriptionPlanSetRepository. There is no
 * SubscriptionPlanSetItemRepository; items are owned entirely by the SubscriptionPlanSet
 * aggregate root.
 */
public interface SubscriptionPlanSetItemDAO extends JpaRepository<SubscriptionPlanSetItemEntity, Long> {

    List<SubscriptionPlanSetItemEntity> findAllBySubscriptionPlanSetId(Long subscriptionPlanSetId);

    void deleteAllBySubscriptionPlanSetId(Long subscriptionPlanSetId);
}
