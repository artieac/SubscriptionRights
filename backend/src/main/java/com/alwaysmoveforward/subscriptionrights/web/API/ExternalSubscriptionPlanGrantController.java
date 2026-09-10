package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.ApplicationService;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanGrantService;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanGrantViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only mirror of the applicable {@link SubscriptionPlanGrantController} endpoints, for
 * API-token callers only, addressed by Application.ExternalId instead of the internal numeric Id
 * -- see com.alwaysmoveforward.subscriptionrights.security.apitoken.ExternalApiTokenAccessGuard.
 */
@RestController
@RequestMapping("/api/external/applications/{externalId}/subscription-plan-grants")
public class ExternalSubscriptionPlanGrantController {

    private final ApplicationService applicationService;
    private final SubscriptionPlanGrantService subscriptionPlanGrantService;

    public ExternalSubscriptionPlanGrantController(ApplicationService applicationService,
                                                     SubscriptionPlanGrantService subscriptionPlanGrantService) {
        this.applicationService = applicationService;
        this.subscriptionPlanGrantService = subscriptionPlanGrantService;
    }

    @GetMapping
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public List<SubscriptionPlanGrantViewModel> list(@PathVariable String externalId,
                                                       @RequestParam(required = false) Long subscriptionPlanId) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return subscriptionPlanGrantService.listForApplication(applicationId, subscriptionPlanId).stream()
                .map(SubscriptionPlanGrantViewModel::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public SubscriptionPlanGrantViewModel get(@PathVariable String externalId, @PathVariable Long id) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return SubscriptionPlanGrantViewModel.from(subscriptionPlanGrantService.getGrant(applicationId, id));
    }
}
