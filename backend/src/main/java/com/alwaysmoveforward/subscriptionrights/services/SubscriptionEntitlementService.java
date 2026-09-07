package com.alwaysmoveforward.subscriptionrights.services;

import com.alwaysmoveforward.subscriptionrights.data.repositories.SubscriptionEntitlementRepository;
import com.alwaysmoveforward.subscriptionrights.data.repositories.ApplicationRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionEntitlement;
import com.alwaysmoveforward.subscriptionrights.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriptionEntitlementService {

    private final SubscriptionEntitlementRepository subscriptionEntitlementRepository;
    private final ApplicationRepository applicationRepository;

    public SubscriptionEntitlementService(SubscriptionEntitlementRepository subscriptionEntitlementRepository,
                                           ApplicationRepository applicationRepository) {
        this.subscriptionEntitlementRepository = subscriptionEntitlementRepository;
        this.applicationRepository = applicationRepository;
    }

    public List<SubscriptionEntitlement> listForApplication(Long applicationId) {
        requireApplication(applicationId);
        return subscriptionEntitlementRepository.findByApplicationId(applicationId);
    }

    public SubscriptionEntitlement getEntitlement(Long applicationId, Long entitlementId) {
        SubscriptionEntitlement entitlement = subscriptionEntitlementRepository.findById(entitlementId)
                .orElseThrow(() -> new NotFoundException("SubscriptionEntitlement " + entitlementId + " not found"));
        requireBelongsToApplication(entitlement, applicationId);
        return entitlement;
    }

    @Transactional
    public SubscriptionEntitlement createEntitlement(Long applicationId, String name, String displayName) {
        requireApplication(applicationId);
        return subscriptionEntitlementRepository.save(SubscriptionEntitlement.create(applicationId, name, displayName));
    }

    @Transactional
    public SubscriptionEntitlement updateEntitlement(Long applicationId, Long entitlementId, String name, String displayName) {
        SubscriptionEntitlement entitlement = getEntitlement(applicationId, entitlementId);
        entitlement.rename(name);
        entitlement.updateDisplayName(displayName);
        return subscriptionEntitlementRepository.save(entitlement);
    }

    @Transactional
    public void deleteEntitlement(Long applicationId, Long entitlementId) {
        getEntitlement(applicationId, entitlementId);
        subscriptionEntitlementRepository.deleteById(entitlementId);
    }

    private void requireApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new NotFoundException("Application " + applicationId + " not found");
        }
    }

    private void requireBelongsToApplication(SubscriptionEntitlement entitlement, Long applicationId) {
        if (!entitlement.getApplicationId().equals(applicationId)) {
            throw new NotFoundException("SubscriptionEntitlement " + entitlement.getId() + " not found for application " + applicationId);
        }
    }
}
