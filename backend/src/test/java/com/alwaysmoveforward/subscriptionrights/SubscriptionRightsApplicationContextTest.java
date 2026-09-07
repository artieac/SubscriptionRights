package com.alwaysmoveforward.subscriptionrights;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Boots the full ApplicationContext. None of this project's other tests do this -- they're all
 * @DataJpaTest slices that never touch SecurityConfig, ConfigurationSettingsConfig, or any
 * ConfigurationSettings-backed bean (JwtProperties, Auth0Properties). Those beans are only
 * actually constructed, and their default-value fallbacks only actually exercised, when the
 * context refreshes -- `mvn package` compiling successfully proves nothing about whether they
 * wire together correctly. This test exists because two real bugs (an unbound Auth0Properties, a
 * null JWT signing secret) both slipped past a clean `mvn package` and were only found by
 * inspection, not by any test -- this closes that gap.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class SubscriptionRightsApplicationContextTest {

    @Test
    void contextLoads() {
    }
}
