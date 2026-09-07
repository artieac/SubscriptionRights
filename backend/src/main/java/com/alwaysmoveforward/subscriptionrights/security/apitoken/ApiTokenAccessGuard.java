package com.alwaysmoveforward.subscriptionrights.security.apitoken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Backs the "@apiTokenAccessGuard.canAccessApplication(#applicationId)" expression used on every
 * read endpoint that's scoped to one Application. A regular (cookie-authenticated) user can read
 * any Application, same as before this existed; an API-token caller (ApiClientPrincipal) can only
 * read the one Application its token was issued for.
 */
@Component("apiTokenAccessGuard")
public class ApiTokenAccessGuard {

    public boolean canAccessApplication(Long applicationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof ApiClientPrincipal apiClientPrincipal) {
            return apiClientPrincipal.getApplicationId().equals(applicationId);
        }
        return true;
    }
}
