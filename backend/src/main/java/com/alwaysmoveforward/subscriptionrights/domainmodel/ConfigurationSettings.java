package com.alwaysmoveforward.subscriptionrights.domainmodel;

import java.util.Map;

/**
 * Every configured setting (name -> its default and whatever the Configuration API returned for
 * it), resolved once and held as the app's single source of truth for configuration lookups —
 * see {@code ConfigurationSettingsConfig} for how the singleton is built.
 */
public final class ConfigurationSettings {

    private final Map<String, ConfigurationSetting> settingsByName;

    public ConfigurationSettings(Map<String, ConfigurationSetting> settingsByName) {
        this.settingsByName = Map.copyOf(settingsByName);
    }

    /** Returns the API value for keyName if present, otherwise its configured default; null if keyName isn't a known setting. */
    public String getConfigurationSetting(String keyName) {
        ConfigurationSetting setting = settingsByName.get(keyName);
        return setting == null ? null : setting.resolvedValue();
    }
}
