package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.ApplicationService;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanSetService;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanSetViewModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Read-only mirror of the applicable {@link SubscriptionPlanSetController} endpoints, for
 * API-token callers only, addressed by Application.ExternalId instead of the internal numeric Id
 * -- see com.alwaysmoveforward.subscriptionrights.security.apitoken.ExternalApiTokenAccessGuard.
 */
@RestController
@RequestMapping("/api/external/applications/{externalId}/subscription-plan-sets")
public class ExternalSubscriptionPlanSetController {

    private final ApplicationService applicationService;
    private final SubscriptionPlanSetService subscriptionPlanSetService;

    public ExternalSubscriptionPlanSetController(ApplicationService applicationService,
                                                  SubscriptionPlanSetService subscriptionPlanSetService) {
        this.applicationService = applicationService;
        this.subscriptionPlanSetService = subscriptionPlanSetService;
    }

    @GetMapping
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public List<SubscriptionPlanSetViewModel> list(@PathVariable String externalId) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return subscriptionPlanSetService.listForApplication(applicationId).stream()
                .map(SubscriptionPlanSetViewModel::from).toList();
    }

    @GetMapping("/active")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public SubscriptionPlanSetViewModel getActive(@PathVariable String externalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return SubscriptionPlanSetViewModel.from(subscriptionPlanSetService.getActiveForApplicationOnDate(applicationId, date));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public SubscriptionPlanSetViewModel get(@PathVariable String externalId, @PathVariable Long id) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        return SubscriptionPlanSetViewModel.from(subscriptionPlanSetService.getSet(applicationId, id));
    }
}
