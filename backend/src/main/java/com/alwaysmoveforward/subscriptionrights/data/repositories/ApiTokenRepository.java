package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApiTokenEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.ApiTokenDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.ApiTokenMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.ApiToken;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class ApiTokenRepository {

    private final ApiTokenDAO apiTokenDAO;
    private final ApiTokenMapper apiTokenMapper;

    public ApiTokenRepository(ApiTokenDAO apiTokenDAO, ApiTokenMapper apiTokenMapper) {
        this.apiTokenDAO = apiTokenDAO;
        this.apiTokenMapper = apiTokenMapper;
    }

    public ApiToken create(Long applicationId, String name, String tokenHash, String tokenPrefix) {
        ApiToken apiToken = ApiToken.create(applicationId, name, tokenHash, tokenPrefix);
        ApiTokenEntity saved = apiTokenDAO.save(apiTokenMapper.toEntity(apiToken));
        return apiTokenMapper.toDomainModel(saved);
    }

    public List<ApiToken> findAllByApplicationId(Long applicationId) {
        return apiTokenDAO.findAllByApplicationId(applicationId).stream()
                .map(apiTokenMapper::toDomainModel).toList();
    }

    /**
     * Empty if no token has this hash, or if it does but has been revoked.
     */
    public Optional<ApiToken> findActiveByTokenHash(String tokenHash) {
        return apiTokenDAO.findByTokenHash(tokenHash)
                .map(apiTokenMapper::toDomainModel)
                .filter(ApiToken::isActive);
    }

    public void revoke(Long id) {
        apiTokenDAO.findById(id).ifPresent(entity -> {
            if (entity.getRevokedAt() == null) {
                entity.setRevokedAt(Instant.now());
                apiTokenDAO.save(entity);
            }
        });
    }
}
