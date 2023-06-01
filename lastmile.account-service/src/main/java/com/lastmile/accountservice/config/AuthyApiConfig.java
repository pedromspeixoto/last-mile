package com.lastmile.accountservice.config;

import com.authy.AuthyApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthyApiConfig {

    @Value("${authy.apikey}")
    private String twillioApiKey;

    @Bean
    public AuthyApiClient authyApiClient() {
        return new AuthyApiClient(twillioApiKey);
    }


}