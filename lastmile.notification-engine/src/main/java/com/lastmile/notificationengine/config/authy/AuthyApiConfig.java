package com.lastmile.notificationengine.config.authy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.authy.AuthyApiClient;

@Configuration
public class AuthyApiConfig {

    @Autowired
    private AuthyApiProperties authyApiProperties;

    @Bean
    public AuthyApiClient authyApiClient() {
      return new AuthyApiClient(authyApiProperties.getApiKey());
    }

}