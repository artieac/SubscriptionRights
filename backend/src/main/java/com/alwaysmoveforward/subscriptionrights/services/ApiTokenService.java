package com.alwaysmoveforward.subscriptionrights.services;

import com.alwaysmoveforward.subscriptionrights.data.repositories.ApiTokenRepository;
import com.alwaysmoveforward.subscriptionrights.data.repositories.ApplicationRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.ApiToken;
import com.alwaysmoveforward.subscriptionrights.exceptions.NotFoundException;
import com.alwaysmoveforward.subscriptionrights.security.apitoken.ApiTokenCrypto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ApiTokenService {

    private final ApiTokenRepository apiTokenRepository;
    private final ApplicationRepository applicationRepository;
    private final ApiTokenCrypto apiTokenCrypto;

    public ApiTokenService(ApiTokenRepository apiTokenRepository, ApplicationRepository applicationRepository,
                            ApiTokenCrypto apiTokenCrypto) {
        this.apiTokenRepository = apiTokenRepository;
        this.applicationRepository = applicationRepository;
        this.apiTokenCrypto = apiTokenCrypto;
    }

    /** The raw token this carries is never persisted -- it exists only long enough to be returned once. */
    public record IssuedApiToken(ApiToken apiToken, String rawToken) {
    }

    @Transactional
    public IssuedApiToken createToken(Long applicationId, String name) {
        requireApplication(applicationId);
        ApiTokenCrypto.GeneratedToken generated = apiTokenCrypto.generate();
        ApiToken apiToken = apiTokenRepository.create(applicationId, name, generated.tokenHash(), generated.tokenPrefix());
        return new IssuedApiToken(apiToken, generated.rawToken());
    }

    public List<ApiToken> listForApplication(Long applicationId) {
        requireApplication(applicationId);
        return apiTokenRepository.findAllByApplicationId(applicationId);
    }

    @Transactional
    public void revokeToken(Long applicationId, Long id) {
        boolean belongsToApplication = apiTokenRepository.findAllByApplicationId(applicationId).stream()
                .anyMatch(token -> token.getId().equals(id));
        if (!belongsToApplication) {
            throw new NotFoundException("ApiToken " + id + " not found for application " + applicationId);
        }
        apiTokenRepository.revoke(id);
    }

    /** Used by ApiTokenAuthenticationFilter to verify a presented token. */
    public Optional<ApiToken> authenticate(String rawToken) {
        return apiTokenRepository.findActiveByTokenHash(apiTokenCrypto.hash(rawToken));
    }

    private void requireApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new NotFoundException("Application " + applicationId + " not found");
        }
    }
}
