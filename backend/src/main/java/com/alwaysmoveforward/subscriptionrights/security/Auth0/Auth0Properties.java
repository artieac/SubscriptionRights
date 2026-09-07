package com.alwaysmoveforward.subscriptionrights.security.Auth0;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Built from ConfigurationSettings (see config/ConfigurationSettingsConfig's auth0Properties
 * bean) rather than bound directly from yml -- domain/clientId/clientSecret/callbackUrl are
 * sourced from the shared Configuration API the same way spring.datasource.* is, each falling
 * back to its configured default-value (subscriptions.config-api.settings) if the API is unset
 * or doesn't have it.
 */
public class Auth0Properties {

    private final String domain;
    private final String clientId;
    private final String clientSecret;
    private final String callbackUrl;

    public Auth0Properties(String domain, String clientId, String clientSecret, String callbackUrl) {
        this.domain = domain;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.callbackUrl = callbackUrl;
    }

    public String getDomain() {
        return domain;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String authorizeUrl(String state) {
        return "https://" + domain + "/authorize"
                + "?response_type=code"
                + "&client_id=" + encode(clientId)
                + "&redirect_uri=" + encode(callbackUrl)
                + "&scope=" + encode("openid profile email")
                + "&state=" + encode(state);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public String tokenUrl() {
        return "https://" + domain + "/oauth/token";
    }

    public String userInfoUrl() {
        return "https://" + domain + "/userinfo";
    }
}
