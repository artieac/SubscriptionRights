package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanEntity;
import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionPlanDAO extends JpaRepository<SubscriptionPlanEntity, SubscriptionPlanEntityId> {

    List<SubscriptionPlanEntity> findAllById_IdOrderById_VersionDesc(Long id);

    @Query("SELECT e FROM SubscriptionPlanEntity e WHERE e.applicationId = :applicationId "
            + "ORDER BY e.id.id ASC, e.id.version DESC")
    List<SubscriptionPlanEntity> findAllByApplicationIdOrderByIdAscVersionDesc(@Param("applicationId") Long applicationId);

    void deleteAllById_Id(Long id);
}
