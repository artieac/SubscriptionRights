package com.alwaysmoveforward.subscriptionrights.data.mapper;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApiTokenEntity;
import com.alwaysmoveforward.subscriptionrights.domainmodel.ApiToken;
import org.springframework.stereotype.Component;

@Component
public class ApiTokenMapper {

    public ApiToken toDomainModel(ApiTokenEntity entity) {
        if (entity == null) {
            return null;
        }
        return ApiToken.reconstitute(entity.getId(), entity.getApplicationId(), entity.getName(),
                entity.getTokenHash(), entity.getTokenPrefix(), entity.getCreatedAt(), entity.getRevokedAt());
    }

    public ApiTokenEntity toEntity(ApiToken domainModel) {
        ApiTokenEntity entity = new ApiTokenEntity();
        entity.setId(domainModel.getId());
        entity.setApplicationId(domainModel.getApplicationId());
        entity.setName(domainModel.getName());
        entity.setTokenHash(domainModel.getTokenHash());
        entity.setTokenPrefix(domainModel.getTokenPrefix());
        entity.setCreatedAt(domainModel.getCreatedAt());
        entity.setRevokedAt(domainModel.getRevokedAt());
        return entity;
    }
}
