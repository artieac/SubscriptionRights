package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationDAO extends JpaRepository<ApplicationEntity, Long> {

    Optional<ApplicationEntity> findByExternalId(String externalId);
}
