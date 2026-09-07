package com.alwaysmoveforward.subscriptionrights.data.mapper;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanEntity;
import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanEntityId;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlan;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPlanMapper {

    public SubscriptionPlan toDomainModel(SubscriptionPlanEntity entity) {
        if (entity == null) {
            return null;
        }
        return SubscriptionPlan.reconstitute(entity.getId().getId(), entity.getId().getVersion(),
                entity.getApplicationId(), entity.getName(), entity.getDescription(), entity.getCreatedAt());
    }

    public SubscriptionPlanEntity toEntity(SubscriptionPlan domainModel) {
        SubscriptionPlanEntity entity = new SubscriptionPlanEntity();
        entity.setId(new SubscriptionPlanEntityId(domainModel.getId(), domainModel.getVersion()));
        entity.setApplicationId(domainModel.getApplicationId());
        entity.setName(domainModel.getName());
        entity.setDescription(domainModel.getDescription());
        entity.setCreatedAt(domainModel.getCreatedAt());
        return entity;
    }
}
