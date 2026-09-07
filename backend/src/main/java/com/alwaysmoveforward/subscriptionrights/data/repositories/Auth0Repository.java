package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.Auth0TokenResponseEntity;
import com.alwaysmoveforward.subscriptionrights.data.Entities.Auth0UserInfoResponseEntity;
import com.alwaysmoveforward.subscriptionrights.domainmodel.Auth0UserProfile;
import com.alwaysmoveforward.subscriptionrights.security.Auth0.Auth0Properties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Calls out to Auth0's OAuth2 endpoints. Translates their raw response shapes
 * (data/Entities) into Domain Models before returning -- callers never see the
 * Auth0*Entity classes.
 */
@Repository
public class Auth0Repository {

    private final Auth0Properties auth0Properties;
    private final RestTemplate restTemplate;

    public Auth0Repository(Auth0Properties auth0Properties) {
        this.auth0Properties = auth0Properties;
        this.restTemplate = new RestTemplate();
    }

    public String exchangeCodeForAccessToken(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", auth0Properties.getClientId());
        form.add("client_secret", auth0Properties.getClientSecret());
        form.add("code", authorizationCode);
        form.add("redirect_uri", auth0Properties.getCallbackUrl());

        Auth0TokenResponseEntity response = restTemplate.postForObject(
                auth0Properties.tokenUrl(), new HttpEntity<>(form, headers), Auth0TokenResponseEntity.class);

        if (response == null || response.getAccessToken() == null) {
            throw new IllegalStateException("Auth0 token exchange returned no access token");
        }
        return response.getAccessToken();
    }

    public Auth0UserProfile fetchUserProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        Auth0UserInfoResponseEntity response = restTemplate.exchange(
                auth0Properties.userInfoUrl(), HttpMethod.GET, new HttpEntity<>(headers),
                Auth0UserInfoResponseEntity.class).getBody();

        if (response == null || response.getSub() == null) {
            throw new IllegalStateException("Auth0 userinfo returned no subject");
        }
        return new Auth0UserProfile(response.getSub(), response.getEmail(), response.getName(), response.getLocale());
    }
}
