package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.ApplicationService;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionEntitlementService;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionEntitlementViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only mirror of the applicable {@link SubscriptionEntitlementController} endpoints, for
 * API-token callers only, addressed by Application.ExternalId instead of the internal numeric Id
 * -- see com.alwaysmoveforward.subscriptionrights.security.apitoken.ExternalApiTokenAccessGuard.
 */
@RestController
@RequestMapping("/api/external/applications/{externalId}/subscription-entitlements")
public class ExternalSubscriptionEntitlementController {

    private final ApplicationService applicationService;
    private final SubscriptionEntitlementService subscriptionEntitlementService;

    public ExternalSubscriptionEntitlementController(ApplicationService applicationService,
                                                       SubscriptionEntitlementService subscriptionEntitlementService) {
        this.applicationService = applicationService;
        this.subscriptionEntitlementService = subscriptionEntitlementService;
    }

    @GetMapping
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public List<SubscriptionEntitlementViewModel> list(@PathVariable String externalId) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return subscriptionEntitlementService.listForApplication(applicationId).stream()
                .map(SubscriptionEntitlementViewModel::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public SubscriptionEntitlementViewModel get(@PathVariable String externalId, @PathVariable Long id) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return SubscriptionEntitlementViewModel.from(subscriptionEntitlementService.getEntitlement(applicationId, id));
    }
}
