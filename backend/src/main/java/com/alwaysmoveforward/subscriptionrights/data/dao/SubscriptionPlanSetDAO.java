package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionPlanSetDAO extends JpaRepository<SubscriptionPlanSetEntity, Long> {

    List<SubscriptionPlanSetEntity> findAllByApplicationId(Long applicationId);

    @Query("SELECT e FROM SubscriptionPlanSetEntity e WHERE e.applicationId = :applicationId "
            + "AND e.effectiveStartDate <= :date "
            + "AND (e.effectiveEndDate IS NULL OR e.effectiveEndDate >= :date)")
    List<SubscriptionPlanSetEntity> findActiveOnDate(@Param("applicationId") Long applicationId,
                                                      @Param("date") LocalDate date);
}
