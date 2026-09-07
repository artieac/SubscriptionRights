package com.alwaysmoveforward.subscriptionrights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SubscriptionRightsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscriptionRightsApplication.class, args);
    }
}
