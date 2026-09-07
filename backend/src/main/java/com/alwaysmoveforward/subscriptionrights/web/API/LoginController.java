package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Kicks off the backend-driven Auth0 login flow. The frontend never talks to Auth0
 * directly -- it just navigates the browser here.
 */
@RestController
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/api/auth/login")
    public void login(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", authService.buildAuthorizeRedirectUrl());
    }
}
