package com.alwaysmoveforward.subscriptionrights.data.mapper;

import com.alwaysmoveforward.subscriptionrights.data.Entities.ApplicationEntity;
import com.alwaysmoveforward.subscriptionrights.domainmodel.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    public Application toDomainModel(ApplicationEntity entity) {
        if (entity == null) {
            return null;
        }
        return Application.reconstitute(entity.getId(), entity.getName(), entity.getExternalId(),
                entity.getDescription(), entity.getCreatedAt());
    }

    public ApplicationEntity toEntity(Application domainModel) {
        ApplicationEntity entity = new ApplicationEntity();
        entity.setId(domainModel.getId());
        entity.setName(domainModel.getName());
        entity.setExternalId(domainModel.getExternalId());
        entity.setDescription(domainModel.getDescription());
        entity.setCreatedAt(domainModel.getCreatedAt());
        return entity;
    }
}
