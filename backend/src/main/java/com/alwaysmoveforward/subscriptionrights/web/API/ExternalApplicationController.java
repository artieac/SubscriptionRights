package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.ApplicationService;
import com.alwaysmoveforward.subscriptionrights.web.Models.ApplicationViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only mirror of the applicable {@link ApplicationController} endpoint, for API-token
 * callers only, addressed by Application.ExternalId instead of the internal numeric Id -- see
 * com.alwaysmoveforward.subscriptionrights.security.apitoken.ExternalApiTokenAccessGuard.
 */
@RestController
@RequestMapping("/api/external/applications")
public class ExternalApplicationController {

    private final ApplicationService applicationService;

    public ExternalApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("@externalApiTokenAccessGuard.canAccess(#externalId)")
    public ApplicationViewModel get(@PathVariable String externalId) {
        return ApplicationViewModel.from(applicationService.getApplicationByExternalId(externalId));
    }
}
