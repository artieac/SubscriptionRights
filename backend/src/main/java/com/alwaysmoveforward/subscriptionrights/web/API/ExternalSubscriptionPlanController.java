package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.domainmodel.SubscriptionPlanGrant;
import com.alwaysmoveforward.subscriptionrights.services.ApplicationService;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanGrantService;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanService;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanGrantVersionGroupViewModel;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanGrantViewModel;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
    private final SubscriptionPlanGrantService subscriptionPlanGrantService;

    public ExternalSubscriptionPlanController(ApplicationService applicationService,
                                               SubscriptionPlanService subscriptionPlanService,
                                               SubscriptionPlanGrantService subscriptionPlanGrantService) {
        this.applicationService = applicationService;
        this.subscriptionPlanService = subscriptionPlanService;
        this.subscriptionPlanGrantService = subscriptionPlanGrantService;
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

    @GetMapping("/{id}/grants")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public List<SubscriptionPlanGrantVersionGroupViewModel> listGrants(@PathVariable String externalId, @PathVariable Long id) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        subscriptionPlanService.getPlan(applicationId, id);
        List<SubscriptionPlanGrant> grants = subscriptionPlanGrantService.listForApplication(applicationId, id);
        TreeMap<Integer, List<SubscriptionPlanGrant>> byVersion = grants.stream()
                .collect(Collectors.groupingBy(SubscriptionPlanGrant::getSubscriptionPlanVersion,
                        () -> new TreeMap<>(Comparator.reverseOrder()), Collectors.toList()));
        return byVersion.entrySet().stream()
                .map(entry -> new SubscriptionPlanGrantVersionGroupViewModel(entry.getKey(),
                        entry.getValue().stream().map(SubscriptionPlanGrantViewModel::from).toList()))
                .toList();
    }

    @GetMapping("/{id}/version/{version}/grants")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public List<SubscriptionPlanGrantViewModel> listGrantsForVersion(@PathVariable String externalId,
            @PathVariable Long id, @PathVariable int version) {
        Long applicationId = applicationService.getApplicationByExternalId(externalId).getId();
        subscriptionPlanService.getPlanVersion(applicationId, id, version);
        return subscriptionPlanGrantService.listForPlanVersion(applicationId, id, version).stream()
                .map(SubscriptionPlanGrantViewModel::from).toList();
    }
}
