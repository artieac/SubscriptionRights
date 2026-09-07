package com.alwaysmoveforward.subscriptionrights.data.mapper;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanGrantEntity;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanGrant;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPlanGrantMapper {

    public SubscriptionPlanGrant toDomainModel(SubscriptionPlanGrantEntity entity) {
        if (entity == null) {
            return null;
        }
        return SubscriptionPlanGrant.reconstitute(entity.getId(), entity.getApplicationId(),
                entity.getSubscriptionPlanId(), entity.getSubscriptionPlanVersion(), entity.getSubscriptionEntitlementId(),
                entity.getValue(), entity.getCreatedAt());
    }

    public SubscriptionPlanGrantEntity toEntity(SubscriptionPlanGrant domainModel) {
        SubscriptionPlanGrantEntity entity = new SubscriptionPlanGrantEntity();
        entity.setId(domainModel.getId());
        entity.setApplicationId(domainModel.getApplicationId());
        entity.setSubscriptionPlanId(domainModel.getSubscriptionPlanId());
        entity.setSubscriptionPlanVersion(domainModel.getSubscriptionPlanVersion());
        entity.setSubscriptionEntitlementId(domainModel.getSubscriptionEntitlementId());
        entity.setValue(domainModel.getValue());
        entity.setCreatedAt(domainModel.getCreatedAt());
        return entity;
    }
}
