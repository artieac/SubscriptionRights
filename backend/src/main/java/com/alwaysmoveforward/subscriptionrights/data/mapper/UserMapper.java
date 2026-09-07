package com.alwaysmoveforward.subscriptionrights.data.mapper;

import com.alwaysmoveforward.subscriptionrights.data.Entities.UserEntity;
import com.alwaysmoveforward.subscriptionrights.domainmodel.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomainModel(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.reconstitute(entity.getId(), entity.getIdentityProviderSubject(), entity.getEmail(),
                entity.getDisplayName(), entity.isAdmin(), entity.getCreatedAt(), entity.getUpdatedAt(),
                entity.getTimeZone(), entity.getLocale());
    }

    public UserEntity toEntity(User domainModel) {
        UserEntity entity = new UserEntity();
        entity.setId(domainModel.getId());
        entity.setIdentityProviderSubject(domainModel.getIdentityProviderSubject());
        entity.setEmail(domainModel.getEmail());
        entity.setDisplayName(domainModel.getDisplayName());
        entity.setAdmin(domainModel.isAdmin());
        entity.setCreatedAt(domainModel.getCreatedAt());
        entity.setUpdatedAt(domainModel.getUpdatedAt());
        entity.setTimeZone(domainModel.getTimeZone());
        entity.setLocale(domainModel.getLocale());
        return entity;
    }
}
