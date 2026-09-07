package com.alwaysmoveforward.subscriptionrights.security.jwt;

/**
 * The authenticated caller, attached to the Spring Security context by
 * {@link JwtCookieAuthenticationFilter} on every request that carries a valid session cookie.
 */
public class AuthenticatedPrincipal {

    private final Long userId;
    private final String email;
    private final String displayName;
    private final boolean admin;

    public AuthenticatedPrincipal(Long userId, String email, String displayName, boolean admin) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.admin = admin;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return admin;
    }
}
