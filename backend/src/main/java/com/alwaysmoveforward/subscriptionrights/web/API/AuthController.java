package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.security.jwt.AuthenticatedPrincipal;
import com.alwaysmoveforward.subscriptionrights.security.jwt.JwtService;
import com.alwaysmoveforward.subscriptionrights.services.UserService;
import com.alwaysmoveforward.subscriptionrights.web.Models.CurrentUserViewModel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/api/auth/me")
    public CurrentUserViewModel me(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return CurrentUserViewModel.from(userService.getUser(principal.getUserId()));
    }

    @PostMapping("/api/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse response) {
        ResponseCookie expired = ResponseCookie.from(jwtService.cookieName(), "")
                .httpOnly(true)
                .secure(jwtService.cookieSecure())
                .domain(jwtService.cookieDomain())
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", expired.toString());
    }
}
