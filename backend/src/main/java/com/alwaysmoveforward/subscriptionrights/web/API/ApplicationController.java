package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.ApplicationService;
import com.alwaysmoveforward.subscriptionrights.web.Models.ApplicationRequest;
import com.alwaysmoveforward.subscriptionrights.web.Models.ApplicationViewModel;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<ApplicationViewModel> list() {
        return applicationService.listApplications().stream().map(ApplicationViewModel::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@apiTokenAccessGuard.canAccessApplication(#id)")
    public ApplicationViewModel get(@PathVariable Long id) {
        return ApplicationViewModel.from(applicationService.getApplication(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApplicationViewModel create(@Valid @RequestBody ApplicationRequest request) {
        return ApplicationViewModel.from(
                applicationService.createApplication(request.getName(), request.getExternalId(), request.getDescription()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApplicationViewModel update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        return ApplicationViewModel.from(
                applicationService.updateApplication(id, request.getName(), request.getExternalId(), request.getDescription()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        applicationService.deleteApplication(id);
    }
}
