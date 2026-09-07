package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.config.ConfigApiProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Talks to the external Configuration API at
 * {@code {base-url}/api/systems/{systemId}/environments/{environmentId}/values}, authenticated
 * with {@code Authorization: Bearer {apiKey}} when an API key is configured. Not JPA-backed —
 * there is no entity/DAO/mapper triad here, just a wire-level
 * {@link ConfigurationEntry} that never leaves this class. Callers only ever see a plain
 * {@code Map<String, String>}, consistent with every other repository never exposing its
 * underlying data-source representation.
 *
 * <p>Resilient by design: an unset base-url or a failed call both just yield an empty map rather
 * than throwing, so callers (ConfigApiEnvironmentPostProcessor, the ConfigurationSettings
 * singleton) never need their own fallback handling — every configured setting already has a
 * default-value to fall back to.
 *
 * <p>The constructor deliberately takes a plain {@link ConfigApiProperties} value (no
 * {@code @Autowired} field/setter injection) so this class can also be {@code new}'d directly by
 * {@code ConfigApiEnvironmentPostProcessor} before the Spring context — and therefore the regular
 * bean of this class — exists yet.
 */
@Repository
public class ConfigurationRepository {

    private static final Log log = LogFactory.getLog(ConfigurationRepository.class);

    private final RestClient restClient;
    private final String systemId;
    private final String environmentId;
    private final boolean configured;

    public ConfigurationRepository(RestClient.Builder restClientBuilder, ConfigApiProperties properties) {
        this.configured = properties.baseUrl() != null && !properties.baseUrl().isBlank();
        restClientBuilder = restClientBuilder.baseUrl(properties.baseUrl() != null ? properties.baseUrl() : "");
        if (properties.apiKey() != null && !properties.apiKey().isBlank()) {
            restClientBuilder = restClientBuilder.defaultHeader("Authorization", "Bearer " + properties.apiKey());
        }
        this.restClient = restClientBuilder.build();
        this.systemId = properties.systemId();
        this.environmentId = properties.environmentId();
    }

    public Map<String, String> getSecrets() {
        if (!configured) {
            log.info("subscriptions.config-api.base-url is not set — returning no secrets; callers fall back to their configured defaults.");
            return Map.of();
        }
        log.info("Calling Configuration API for secrets — systemId=" + systemId + ", environmentId=" + environmentId);
        try {
            ConfigurationEntry[] entries = restClient.get()
                    .uri("/api/systems/{systemId}/environments/{environmentId}/values", systemId, environmentId)
                    .retrieve()
                    .body(ConfigurationEntry[].class);

            if (entries == null) {
                log.error("Configuration API returned no body for secrets — callers fall back to their configured defaults.");
                return Map.of();
            }
            return Arrays.stream(entries).collect(Collectors.toMap(ConfigurationEntry::configurationName, entry -> trim(entry.value())));
        } catch (RuntimeException e) {
            log.error("Failed to load configuration from the Configuration API — callers fall back to their configured defaults.", e);
            return Map.of();
        }
    }

    /** Trailing/leading whitespace on a returned secret (copy-paste artifacts, trailing newlines) causes exact-match failures downstream — e.g. a DB username with a trailing space is a different, nonexistent user. */
    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private record ConfigurationEntry(Long configurationId, Long environmentId, String configurationName, String environmentName, String value) {
    }
}
