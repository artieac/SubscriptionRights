package com.alwaysmoveforward.subscriptionrights.security.apitoken;

import com.alwaysmoveforward.subscriptionrights.data.repositories.ApplicationRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.Application;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Backs the "@externalApiTokenAccessGuard.canAccess(#externalId)" expression used on every
 * /api/external/applications/{externalId}/... route. Unlike {@link ApiTokenAccessGuard}, these
 * routes exist only for API-token callers -- a cookie-authenticated browser session is rejected
 * here even though it would otherwise be authenticated, since the whole point of this route family
 * is to give external systems a stable, name-like identifier that isn't the internal numeric Id.
 */
@Component("externalApiTokenAccessGuard")
public class ExternalApiTokenAccessGuard {

    private final ApplicationRepository applicationRepository;

    public ExternalApiTokenAccessGuard(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public boolean canAccess(String externalId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication.getPrincipal() instanceof ApiClientPrincipal apiClientPrincipal)) {
            return false;
        }
        Optional<Application> application = applicationRepository.findByExternalId(externalId);
        return application.isPresent() && apiClientPrincipal.getApplicationId().equals(application.get().getId());
    }
}
