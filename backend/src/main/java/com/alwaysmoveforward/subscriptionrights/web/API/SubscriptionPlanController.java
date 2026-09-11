package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.GrantValueInput;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanGrantService;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanService;
import com.alwaysmoveforward.subscriptionrights.web.Models.ReplaceSubscriptionPlanGrantsRequest;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanRequest;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanViewModel;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionPlanGrantService subscriptionPlanGrantService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService,
                                       SubscriptionPlanGrantService subscriptionPlanGrantService) {
        this.subscriptionPlanService = subscriptionPlanService;
        this.subscriptionPlanGrantService = subscriptionPlanGrantService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<SubscriptionPlanViewModel> list(@PathVariable Long applicationId) {
        return subscriptionPlanService.listForApplication(applicationId).stream()
                .map(SubscriptionPlanViewModel::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public SubscriptionPlanViewModel get(@PathVariable Long applicationId, @PathVariable Long id) {
        return SubscriptionPlanViewModel.from(subscriptionPlanService.getPlan(applicationId, id));
    }

    @GetMapping("/{id}/versions")
    @PreAuthorize("hasRole('USER')")
    public List<SubscriptionPlanViewModel> listVersions(@PathVariable Long applicationId, @PathVariable Long id) {
        return subscriptionPlanService.listVersions(applicationId, id).stream()
                .map(SubscriptionPlanViewModel::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionPlanViewModel create(@PathVariable Long applicationId, @Valid @RequestBody SubscriptionPlanRequest request) {
        return SubscriptionPlanViewModel.from(
                subscriptionPlanService.createPlan(applicationId, request.getName(), request.getDescription()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionPlanViewModel update(@PathVariable Long applicationId, @PathVariable Long id,
                                             @Valid @RequestBody SubscriptionPlanRequest request) {
        return SubscriptionPlanViewModel.from(
                subscriptionPlanService.updatePlan(applicationId, id, request.getName(), request.getDescription()));
    }

    /**
     * Replaces this plan's whole set of entitlement grants as one unit: creates a new plan
     * version and a fresh grant per submitted item, pinned to it. See
     * SubscriptionPlanGrantService#replaceGrantsForPlan.
     */
    @PutMapping("/{id}/grants")
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionPlanViewModel replaceGrants(@PathVariable Long applicationId, @PathVariable Long id,
                                                    @Valid @RequestBody ReplaceSubscriptionPlanGrantsRequest request) {
        List<GrantValueInput> items = request.getItems().stream()
                .map(item -> new GrantValueInput(item.getSubscriptionEntitlementId(), item.getValue()))
                .toList();
        return SubscriptionPlanViewModel.from(subscriptionPlanGrantService.replaceGrantsForPlan(
                applicationId, id, items, request.isCreateNewVersion(), request.getTargetVersion()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long applicationId, @PathVariable Long id) {
        subscriptionPlanService.deletePlan(applicationId, id);
    }
}
