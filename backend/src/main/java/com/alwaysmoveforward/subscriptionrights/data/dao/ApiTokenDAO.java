package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApiTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiTokenDAO extends JpaRepository<ApiTokenEntity, Long> {

    List<ApiTokenEntity> findAllByApplicationId(Long applicationId);

    Optional<ApiTokenEntity> findByTokenHash(String tokenHash);
}
