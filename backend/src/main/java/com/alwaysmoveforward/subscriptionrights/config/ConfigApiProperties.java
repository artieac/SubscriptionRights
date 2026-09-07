package com.alwaysmoveforward.subscriptionrights.config;

import com.alwaysmoveforward.subscriptionrights.domainmodel.ConfigurationSetting;
import com.alwaysmoveforward.subscriptionrights.domainmodel.ConfigurationSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Binds the whole `subscriptions.config-api` section: where the Configuration API is, which
 * system/environment this instance asks for, and — per logical setting name — which key to look
 * up in the API's response and what to use if that key (or the API itself) isn't available.
 *
 * <p>Adding a new API-backed setting is just adding an entry under `settings` in
 * application.yml; no Java changes needed unless something also has to bootstrap it before the
 * Spring context exists (see ConfigApiEnvironmentPostProcessor for the datasource settings).
 */
@ConfigurationProperties(prefix = "subscriptions.config-api")
public record ConfigApiProperties(String baseUrl, String systemId, String environmentId, String apiKey, Map<String, Setting> settings) {

    public record Setting(String key, String defaultValue) {
    }

    /** Combines each configured setting's default with whatever the Configuration API actually returned. */
    public ConfigurationSettings toConfigurationSettings(Map<String, String> apiValues) {
        Map<String, ConfigurationSetting> resolved = new HashMap<>();
        if (settings != null) {
            settings.forEach((name, setting) ->
                    resolved.put(name, new ConfigurationSetting(setting.defaultValue(), apiValues.get(setting.key()))));
        }
        return new ConfigurationSettings(resolved);
    }
}
