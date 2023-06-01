package com.lastmile.notificationengine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceProperties {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.profiles.active}")
    private String environment;

    public String getName() {
        return this.name;
    }

    public String getEnvironment() {
        return this.environment;
    }
}