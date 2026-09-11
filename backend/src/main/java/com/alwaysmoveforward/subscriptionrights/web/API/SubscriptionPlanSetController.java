package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanSetItemInput;
import com.alwaysmoveforward.subscriptionrights.services.SubscriptionPlanSetService;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanSetItemRequest;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanSetRequest;
import com.alwaysmoveforward.subscriptionrights.web.Models.SubscriptionPlanSetViewModel;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}/subscription-plan-sets")
public class SubscriptionPlanSetController {

    private final SubscriptionPlanSetService subscriptionPlanSetService;

    public SubscriptionPlanSetController(SubscriptionPlanSetService subscriptionPlanSetService) {
        this.subscriptionPlanSetService = subscriptionPlanSetService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<SubscriptionPlanSetViewModel> list(@PathVariable Long applicationId) {
        return subscriptionPlanSetService.listForApplication(applicationId).stream()
                .map(SubscriptionPlanSetViewModel::from).toList();
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('USER')")
    public SubscriptionPlanSetViewModel getActive(@PathVariable Long applicationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return SubscriptionPlanSetViewModel.from(subscriptionPlanSetService.getActiveForApplicationOnDate(applicationId, date));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public SubscriptionPlanSetViewModel get(@PathVariable Long applicationId, @PathVariable Long id) {
        return SubscriptionPlanSetViewModel.from(subscriptionPlanSetService.getSet(applicationId, id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionPlanSetViewModel create(@PathVariable Long applicationId,
                                                @Valid @RequestBody SubscriptionPlanSetRequest request) {
        return SubscriptionPlanSetViewModel.from(subscriptionPlanSetService.create(applicationId, request.getName(),
                request.getEffectiveStartDate(), request.getEffectiveEndDate(), toItemInputs(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionPlanSetViewModel update(@PathVariable Long applicationId, @PathVariable Long id,
                                                @Valid @RequestBody SubscriptionPlanSetRequest request) {
        return SubscriptionPlanSetViewModel.from(subscriptionPlanSetService.update(applicationId, id, request.getName(),
                request.getEffectiveStartDate(), request.getEffectiveEndDate(), toItemInputs(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long applicationId, @PathVariable Long id) {
        subscriptionPlanSetService.delete(applicationId, id);
    }

    private List<SubscriptionPlanSetItemInput> toItemInputs(SubscriptionPlanSetRequest request) {
        return request.getItems().stream()
                .map((SubscriptionPlanSetItemRequest item) -> new SubscriptionPlanSetItemInput(
                        item.getSubscriptionPlanId(), item.getSubscriptionPlanVersion(), item.getTier()))
                .toList();
    }
}
