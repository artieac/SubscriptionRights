package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.SubscriptionEntitlementEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.SubscriptionEntitlementDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.SubscriptionEntitlementMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionEntitlement;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionEntitlementRepository {

    private final SubscriptionEntitlementDAO subscriptionEntitlementDAO;
    private final SubscriptionEntitlementMapper subscriptionEntitlementMapper;

    public SubscriptionEntitlementRepository(SubscriptionEntitlementDAO subscriptionEntitlementDAO,
                                              SubscriptionEntitlementMapper subscriptionEntitlementMapper) {
        this.subscriptionEntitlementDAO = subscriptionEntitlementDAO;
        this.subscriptionEntitlementMapper = subscriptionEntitlementMapper;
    }

    public List<SubscriptionEntitlement> findByApplicationId(Long applicationId) {
        return subscriptionEntitlementDAO.findByApplicationId(applicationId).stream()
                .map(subscriptionEntitlementMapper::toDomainModel).toList();
    }

    public Optional<SubscriptionEntitlement> findById(Long id) {
        return subscriptionEntitlementDAO.findById(id).map(subscriptionEntitlementMapper::toDomainModel);
    }

    public SubscriptionEntitlement save(SubscriptionEntitlement subscriptionEntitlement) {
        SubscriptionEntitlementEntity saved = subscriptionEntitlementDAO.save(subscriptionEntitlementMapper.toEntity(subscriptionEntitlement));
        return subscriptionEntitlementMapper.toDomainModel(saved);
    }

    public void deleteById(Long id) {
        subscriptionEntitlementDAO.deleteById(id);
    }
}
