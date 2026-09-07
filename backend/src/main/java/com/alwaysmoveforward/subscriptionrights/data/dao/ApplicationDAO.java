package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationDAO extends JpaRepository<ApplicationEntity, Long> {
}
