package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanGrantService;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanGrantRequest;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanGrantViewModel;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}/subscription-plan-grants")
public class SubscriptionPlanGrantController {

    private final SubscriptionPlanGrantService subscriptionPlanGrantService;

    public SubscriptionPlanGrantController(SubscriptionPlanGrantService subscriptionPlanGrantService) {
        this.subscriptionPlanGrantService = subscriptionPlanGrantService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<SubscriptionPlanGrantViewModel> list(@PathVariable Long applicationId,
                                                       @RequestParam(required = false) Long subscriptionPlanId) {
        return subscriptionPlanGrantService.listForApplication(applicationId, subscriptionPlanId).stream()
                .map(SubscriptionPlanGrantViewModel::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public SubscriptionPlanGrantViewModel get(@PathVariable Long applicationId, @PathVariable Long id) {
        return SubscriptionPlanGrantViewModel.from(subscriptionPlanGrantService.getGrant(applicationId, id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionPlanGrantViewModel create(@PathVariable Long applicationId,
                                                  @Valid @RequestBody SubscriptionPlanGrantRequest request) {
        return SubscriptionPlanGrantViewModel.from(subscriptionPlanGrantService.createGrant(
                applicationId, request.getSubscriptionPlanId(), request.getSubscriptionEntitlementId(), request.getValue()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long applicationId, @PathVariable Long id) {
        subscriptionPlanGrantService.deleteGrant(applicationId, id);
    }
}
