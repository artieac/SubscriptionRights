package com.alwaysmoveforward.subscriptionrights.data.dao;

import com.alwaysmoveforward.subscriptionrights.data.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDAO extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByIdentityProviderSubject(String identityProviderSubject);

    Optional<UserEntity> findByEmail(String email);
}
