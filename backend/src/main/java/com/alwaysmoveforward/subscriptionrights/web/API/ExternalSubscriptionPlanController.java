package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.ApplicationService;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanService;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only mirror of the applicable {@link SubscriptionPlanController} endpoints, for API-token
 * callers only, addressed by Application.ExternalId instead of the internal numeric Id -- see
 * com.alwaysmoveforward.subscriptionrights.security.apitoken.ExternalApiTokenAccessGuard.
 */
@RestController
@RequestMapping("/api/external/applications/{externalId}/subscription-plans")
public class ExternalSubscriptionPlanController {

    private final ApplicationService applicationService;
    private final SubscriptionPlanService subscriptionPlanService;

    public ExternalSubscriptionPlanController(ApplicationService applicationService,
                                               SubscriptionPlanService subscriptionPlanService) {
        this.applicationService = applicationService;
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public List<SubscriptionPlanViewModel> list(@PathVariable String externalId) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return subscriptionPlanService.listForApplication(applicationId).stream()
                .map(SubscriptionPlanViewModel::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public SubscriptionPlanViewModel get(@PathVariable String externalId, @PathVariable Long id) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return SubscriptionPlanViewModel.from(subscriptionPlanService.getPlan(applicationId, id));
    }

    @GetMapping("/{id}/versions")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public List<SubscriptionPlanViewModel> listVersions(@PathVariable String externalId, @PathVariable Long id) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return subscriptionPlanService.listVersions(applicationId, id).stream()
                .map(SubscriptionPlanViewModel::from).toList();
    }
}
