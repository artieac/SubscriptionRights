package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.ApiTokenService;
import com.alwaysmoveforward.subscriptionrights.web.Models.ApiTokenRequest;
import com.alwaysmoveforward.subscriptionrights.web.Models.ApiTokenViewModel;
import com.alwaysmoveforward.subscriptionrights.web.Models.IssuedApiTokenViewModel;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Token management is an admin action -- every method here requires ROLE_ADMIN. This is
 * distinct from ExternalApiTokenAccessGuard, which governs what a token itself can read once issued.
 */
@RestController
@RequestMapping("/api/applications/{applicationId}/api-tokens")
@PreAuthorize("hasRole('ADMIN')")
public class ApiTokenController {

    private final ApiTokenService apiTokenService;

    public ApiTokenController(ApiTokenService apiTokenService) {
        this.apiTokenService = apiTokenService;
    }

    @GetMapping
    public List<ApiTokenViewModel> list(@PathVariable Long applicationId) {
        return apiTokenService.listForApplication(applicationId).stream().map(ApiTokenViewModel::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssuedApiTokenViewModel create(@PathVariable Long applicationId, @Valid @RequestBody ApiTokenRequest request) {
        return IssuedApiTokenViewModel.from(apiTokenService.createToken(applicationId, request.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable Long applicationId, @PathVariable Long id) {
        apiTokenService.revokeToken(applicationId, id);
    }
}
