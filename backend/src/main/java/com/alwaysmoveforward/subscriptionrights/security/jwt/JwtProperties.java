package com.alwaysmoveforward.subscriptionrights.security.jwt;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Built from ConfigurationSettings (see config/ConfigurationSettingsConfig's jwtProperties bean)
 * rather than bound directly from yml -- the signing secret and the session cookie's
 * name/domain/secure-flag/expiration are all sourced from the shared Configuration API the same
 * way Auth0Properties's fields are, each falling back to its configured default-value
 * (subscriptions.config-api.settings) if the API is unset or doesn't have it.
 */
public class JwtProperties {

    private final String secret;
    private final String cookieName;
    private final String cookieDomain;
    private final boolean cookieSecure;
    private final long cookieExpirationMinutes;

    public JwtProperties(String secret, String cookieName, String cookieDomain, boolean cookieSecure,
                          long cookieExpirationMinutes) {
        this.secret = secret;
        this.cookieName = cookieName;
        this.cookieDomain = cookieDomain;
        this.cookieSecure = cookieSecure;
        this.cookieExpirationMinutes = cookieExpirationMinutes;
    }

    public String getCookieName() {
        return cookieName;
    }

    public String getCookieDomain() {
        return cookieDomain;
    }

    public boolean isCookieSecure() {
        return cookieSecure;
    }

    public long getCookieExpirationMinutes() {
        return cookieExpirationMinutes;
    }

    public SecretKey toSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
