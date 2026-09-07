package com.alwaysmoveforward.subscriptionrights.domainmodel;

/** One named configuration setting: what to use if the Configuration API doesn't have it, and what it actually returned (if anything). */
public record ConfigurationSetting(String defaultValue, String apiValue) {

    public String resolvedValue() {
        return apiValue != null ? apiValue : defaultValue;
    }
}
