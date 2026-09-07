package com.alwaysmoveforward.subscriptionrights.services;

import com.alwaysmoveforward.subscriptionrights.data.repositories.Auth0Repository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.Auth0UserProfile;
import com.alwaysmoveforward.subscriptionrights.domainmodel.User;
import com.alwaysmoveforward.subscriptionrights.exceptions.DomainException;
import com.alwaysmoveforward.subscriptionrights.security.Auth0.Auth0Properties;
import com.alwaysmoveforward.subscriptionrights.security.jwt.JwtService;
import com.alwaysmoveforward.subscriptionrights.security.jwt.LoginStateService;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the backend-driven Auth0 OAuth2 login flow: build the authorize
 * redirect, then on callback exchange the code, provision/refresh the User, and
 * issue the session JWT. The frontend never talks to Auth0 directly.
 */
@Service
public class AuthService {

    private final Auth0Properties auth0Properties;
    private final Auth0Repository auth0Repository;
    private final LoginStateService loginStateService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(Auth0Properties auth0Properties, Auth0Repository auth0Repository,
                        LoginStateService loginStateService, UserService userService, JwtService jwtService) {
        this.auth0Properties = auth0Properties;
        this.auth0Repository = auth0Repository;
        this.loginStateService = loginStateService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public String buildAuthorizeRedirectUrl() {
        return auth0Properties.authorizeUrl(loginStateService.issueState());
    }

    public String completeLogin(String code, String state) {
        if (!loginStateService.isValid(state)) {
            throw new DomainException("Invalid or expired login state");
        }
        String accessToken = auth0Repository.exchangeCodeForAccessToken(code);
        Auth0UserProfile profile = auth0Repository.fetchUserProfile(accessToken);
        User user = userService.findOrCreateFromAuth0Profile(profile);
        return jwtService.issueToken(user.getId());
    }
}
