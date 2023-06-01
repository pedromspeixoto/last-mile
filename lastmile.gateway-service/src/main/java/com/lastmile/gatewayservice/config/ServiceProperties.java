package com.lastmile.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceProperties {

    @Value("${lastmile.ssl.enabled}")
    private String sslEnabled = "";

    @Value("${lastmile.swagger.enabled}")
    private String swaggerEnabled = "";

    public String getSslEnabled() {
	return sslEnabled;
    }

    public String getSwaggerEnabled() {
	return swaggerEnabled;
    }

}