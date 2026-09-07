package com.alwaysmoveforward.subscriptionrights.config;

import com.alwaysmoveforward.subscriptionrights.data.repositories.ConfigurationRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.ConfigurationSettings;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves every Spring property that's needed before the ApplicationContext itself is built —
 * just spring.datasource.url/username/password (read by DataSourceAutoConfiguration) — the same
 * way ConfigurationSettings resolves any other setting: via
 * {@link ConfigApiProperties#toConfigurationSettings} + {@link ConfigurationSettings#getConfigurationSetting}.
 * ConfigurationRepository itself already falls back to an empty secrets map if the API is unset
 * or unreachable, so every setting here just falls through to its configured default-value in
 * that case — the app always ends up with usable values.
 *
 * <p>Only settings a Spring Boot auto-configuration needs before any regular {@code @Bean} can
 * run belong here — that's just the datasource (built by {@code DataSourceAutoConfiguration}
 * strictly before the context finishes refreshing). Everything else this app sources from the
 * Configuration API (Auth0's settings, the JWT secret) is read normally, after context startup,
 * straight off the {@code ConfigurationSettings} singleton bean — see
 * {@code ConfigurationSettingsConfig}.
 *
 * <p>This runs before the ApplicationContext exists, so it can't use the regular
 * ConfigurationRepository/ConfigApiProperties beans — it binds ConfigApiProperties straight off
 * the Environment with {@link Binder} (the same mechanism Spring Boot itself uses this early) and
 * constructs a ConfigurationRepository directly.
 *
 * <p>Logging this early is normally silently dropped — the real logging system (Logback) isn't
 * initialized yet when EnvironmentPostProcessors run — unless you go through the {@link Log}
 * Spring Boot hands you via a {@link DeferredLogFactory} constructor, which buffers messages and
 * replays them once logging is ready. That's why this takes one instead of just calling
 * {@code LogFactory.getLog(...)} directly.
 */
public class ConfigApiEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * target Spring property -> logical setting name under subscriptions.config-api.settings.
     * Only properties that a Spring Boot auto-configuration reads before the ApplicationContext
     * (and therefore before any regular @Bean, including the ConfigurationSettings singleton)
     * exists belong here — that's just the datasource. Auth0's settings are consumed by our own
     * Auth0Properties @Bean (see ConfigurationSettingsConfig), which runs at normal bean-creation
     * time and can read ConfigurationSettings directly, the same way the JWT secret already does.
     */
    private static final Map<String, String> BOOTSTRAP_SETTINGS = Map.of(
            "spring.datasource.url", "db-url",
            "spring.datasource.username", "db-username",
            "spring.datasource.password", "db-password");

    /** Never worth logging even at resolved-value granularity. */
    private static final Map<String, String> SENSITIVE_PROPERTIES = Map.of(
            "spring.datasource.password", "spring.datasource.password");

    private final Log log;

    public ConfigApiEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(ConfigApiEnvironmentPostProcessor.class);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        ConfigApiProperties properties = Binder.get(environment)
                .bind("subscriptions.config-api", ConfigApiProperties.class)
                .orElse(null);

        if (properties == null || properties.settings() == null) {
            log.warn("subscriptions.config-api.settings is not configured — spring.datasource.* and the Auth0 client "
                    + "credentials must be set directly.");
            return;
        }

        ConfigurationRepository configurationRepository = new ConfigurationRepository(RestClient.builder(), properties);
        Map<String, String> apiValues = configurationRepository.getSecrets();
        ConfigurationSettings configurationSettings = properties.toConfigurationSettings(apiValues);

        Map<String, Object> resolved = new HashMap<>();
        BOOTSTRAP_SETTINGS.forEach((targetProperty, settingName) -> {
            String value = configurationSettings.getConfigurationSetting(settingName);
            if (value != null) {
                resolved.put(targetProperty, value);
            }
        });

        if (!resolved.isEmpty()) {
            // addLast, not addFirst: this is a fallback, not an override -- an explicit
            // spring.datasource.* set elsewhere (e.g. the test profile's own H2 config) must
            // still win. Production never sets spring.datasource.* anywhere else, so this is
            // the only place those properties resolve from there -- addLast doesn't change
            // that, a property source at the back of the list is still consulted whenever
            // nothing ahead of it defines the key.
            environment.getPropertySources().addLast(new MapPropertySource("configApiProperties", resolved));
        }

        log.info("Bootstrap config resolved from subscriptions.config-api (base-url=" + properties.baseUrl()
                + ", keys returned by API=" + apiValues.keySet() + "): " + describe(resolved));
    }

    private String describe(Map<String, Object> resolved) {
        StringBuilder description = new StringBuilder();
        BOOTSTRAP_SETTINGS.keySet().stream().sorted().forEach(targetProperty -> {
            if (description.length() > 0) {
                description.append(", ");
            }
            description.append(targetProperty).append('=');
            description.append(SENSITIVE_PROPERTIES.containsKey(targetProperty)
                    ? (resolved.get(targetProperty) != null ? "<set>" : "<unset>")
                    : resolved.get(targetProperty));
        });
        return description.toString();
    }

    @Override
    public int getOrder() {
        // Must be strictly greater than ConfigDataEnvironmentPostProcessor.ORDER (itself
        // HIGHEST_PRECEDENCE + 10 — using that same value here ties the two, and Spring doesn't
        // guarantee ties resolve in registration order, so application.yml was sometimes not
        // loaded yet when this ran and subscriptions.config-api.* resolved to nothing). This must run
        // strictly after it.
        return ConfigDataEnvironmentPostProcessor.ORDER + 1;
    }
}
