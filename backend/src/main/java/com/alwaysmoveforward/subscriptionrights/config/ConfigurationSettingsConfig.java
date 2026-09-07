package com.alwaysmoveforward.subscriptionrights.config;

import com.alwaysmoveforward.subscriptionrights.data.repositories.ConfigurationRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.ConfigurationSettings;
import com.alwaysmoveforward.subscriptionrights.security.AdminEmailsProperties;
import com.alwaysmoveforward.subscriptionrights.security.Auth0.Auth0Properties;
import com.alwaysmoveforward.subscriptionrights.security.jwt.JwtProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Fetches the Configuration API once at startup; the result is held as a singleton bean for the life of the app. */
@Configuration
public class ConfigurationSettingsConfig {

    @Bean
    public ConfigurationSettings configurationSettings(ConfigurationRepository configurationRepository, ConfigApiProperties properties) {
        return properties.toConfigurationSettings(configurationRepository.getSecrets());
    }

    /**
     * Built from ConfigurationSettings rather than bound directly from yml -- see
     * Auth0Properties's own class comment.
     */
    @Bean
    public Auth0Properties auth0Properties(ConfigurationSettings configurationSettings) {
        return new Auth0Properties(
                configurationSettings.getConfigurationSetting("auth0-domain"),
                configurationSettings.getConfigurationSetting("auth0-client-id"),
                configurationSettings.getConfigurationSetting("auth0-client-secret"),
                configurationSettings.getConfigurationSetting("auth0-callback-url"));
    }

    /**
     * Built from ConfigurationSettings rather than bound directly from yml -- see
     * JwtProperties's own class comment.
     */
    @Bean
    public JwtProperties jwtProperties(ConfigurationSettings configurationSettings) {
        return new JwtProperties(
                configurationSettings.getConfigurationSetting("app-jwt-secret"),
                configurationSettings.getConfigurationSetting("app-jwt-cookie-name"),
                configurationSettings.getConfigurationSetting("app-jwt-cookie-domain"),
                Boolean.parseBoolean(configurationSettings.getConfigurationSetting("app-jwt-cookie-secure")),
                Long.parseLong(configurationSettings.getConfigurationSetting("app-jwt-cookie-expiration-minutes")));
    }

    /**
     * Built from ConfigurationSettings rather than bound directly from yml -- see
     * AdminEmailsProperties's own class comment.
     */
    @Bean
    public AdminEmailsProperties adminEmailsProperties(ConfigurationSettings configurationSettings) {
        return new AdminEmailsProperties(configurationSettings.getConfigurationSetting("admin-emails"));
    }
}
