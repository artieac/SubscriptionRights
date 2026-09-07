package com.alwaysmoveforward.subscriptionrights.security.apitoken;

/**
 * A machine caller authenticated via an API token, attached to the Spring Security context by
 * {@link ApiTokenAuthenticationFilter}. Carries ROLE_API_CLIENT, an authority deliberately outside
 * the ROLE_USER/ROLE_ADMIN hierarchy -- a token isn't comparable to a logged-in user's role, it's
 * a narrower, Application-scoped, read-only credential (see ApiTokenAccessGuard).
 */
public class ApiClientPrincipal {

    private final Long apiTokenId;
    private final Long applicationId;

    public ApiClientPrincipal(Long apiTokenId, Long applicationId) {
        this.apiTokenId = apiTokenId;
        this.applicationId = applicationId;
    }

    public Long getApiTokenId() {
        return apiTokenId;
    }

    public Long getApplicationId() {
        return applicationId;
    }
}
