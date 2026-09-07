package com.alwaysmoveforward.subscriptionrights.data.mapper;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanSetEntity;
import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionPlanSetItemEntity;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSet;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanSetItem;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Maps SubscriptionPlanSet <-> SubscriptionPlanSetEntity/SubscriptionPlanSetItemEntity.
 * Used only by SubscriptionPlanSetRepository -- items are part of the aggregate, not a
 * separately-mapped concern callers ever see.
 */
@Component
public class SubscriptionPlanSetMapper {

    public SubscriptionPlanSet toDomainModel(SubscriptionPlanSetEntity entity, List<SubscriptionPlanSetItemEntity> itemEntities) {
        if (entity == null) {
            return null;
        }
        List<SubscriptionPlanSetItem> items = itemEntities.stream()
                .map(this::toItemDomainModel)
                .toList();
        return SubscriptionPlanSet.reconstitute(entity.getId(), entity.getApplicationId(), entity.getName(),
                entity.getEffectiveStartDate(), entity.getEffectiveEndDate(), items,
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public SubscriptionPlanSetEntity toEntity(SubscriptionPlanSet domainModel) {
        SubscriptionPlanSetEntity entity = new SubscriptionPlanSetEntity();
        entity.setId(domainModel.getId());
        entity.setApplicationId(domainModel.getApplicationId());
        entity.setName(domainModel.getName());
        entity.setEffectiveStartDate(domainModel.getEffectiveStartDate());
        entity.setEffectiveEndDate(domainModel.getEffectiveEndDate());
        entity.setCreatedAt(domainModel.getCreatedAt());
        entity.setUpdatedAt(domainModel.getUpdatedAt());
        return entity;
    }

    public List<SubscriptionPlanSetItemEntity> toItemEntities(Long subscriptionPlanSetId, SubscriptionPlanSet domainModel) {
        Instant now = Instant.now();
        return domainModel.getItems().stream().map(item -> {
            SubscriptionPlanSetItemEntity entity = new SubscriptionPlanSetItemEntity();
            entity.setSubscriptionPlanSetId(subscriptionPlanSetId);
            entity.setSubscriptionPlanId(item.getSubscriptionPlanId());
            entity.setSubscriptionPlanVersion(item.getSubscriptionPlanVersion());
            entity.setTier(item.getTier());
            entity.setCreatedAt(now);
            return entity;
        }).toList();
    }

    private SubscriptionPlanSetItem toItemDomainModel(SubscriptionPlanSetItemEntity entity) {
        return SubscriptionPlanSetItem.of(entity.getSubscriptionPlanId(), entity.getSubscriptionPlanVersion(), entity.getTier());
    }
}
