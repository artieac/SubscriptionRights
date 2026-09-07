package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.security.FrontendProperties;
import com.alwaysmoveforward.subscriptionrights.security.jwt.JwtService;
import com.alwaysmoveforward.subscriptionrights.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;

/**
 * Handles the Auth0 redirect back to us: exchanges the code, provisions/refreshes
 * the User, issues the session JWT as an HttpOnly cookie, then sends the browser
 * on to the frontend app.
 */
@RestController
public class CallbackController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final FrontendProperties frontendProperties;

    public CallbackController(AuthService authService, JwtService jwtService, FrontendProperties frontendProperties) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.frontendProperties = frontendProperties;
    }

    @GetMapping("/api/auth/callback")
    public void callback(@RequestParam String code, @RequestParam String state, HttpServletResponse response)
            throws IOException {
        String token = authService.completeLogin(code, state);

        ResponseCookie cookie = ResponseCookie.from(jwtService.cookieName(), token)
                .httpOnly(true)
                .secure(jwtService.cookieSecure())
                .domain(jwtService.cookieDomain())
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofSeconds(jwtService.expirationSeconds()))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", frontendProperties.getBaseUrl());
    }
}
