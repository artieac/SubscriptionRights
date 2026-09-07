package com.alwaysmoveforward.subscriptionrights.data.mapper;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionEntitlementEntity;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionEntitlement;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionEntitlementMapper {

    public SubscriptionEntitlement toDomainModel(SubscriptionEntitlementEntity entity) {
        if (entity == null) {
            return null;
        }
        return SubscriptionEntitlement.reconstitute(entity.getId(), entity.getApplicationId(), entity.getName(),
                entity.getDisplayName(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public SubscriptionEntitlementEntity toEntity(SubscriptionEntitlement domainModel) {
        SubscriptionEntitlementEntity entity = new SubscriptionEntitlementEntity();
        entity.setId(domainModel.getId());
        entity.setApplicationId(domainModel.getApplicationId());
        entity.setName(domainModel.getName());
        entity.setDisplayName(domainModel.getDisplayName());
        entity.setCreatedAt(domainModel.getCreatedAt());
        entity.setUpdatedAt(domainModel.getUpdatedAt());
        return entity;
    }
}
