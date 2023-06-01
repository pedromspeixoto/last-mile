package com.lastmile.gatewayservice.client;

import java.time.LocalDateTime;
import java.util.UUID;

import com.google.gson.Gson;
import com.lastmile.gatewayservice.dto.AuthServerUserDetailsDto;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.logs.CustomLogging;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class OAuthTokenClient {
    
    @Value( "${security.oauth2.client.accessTokenUri}" )
    private String GET_LOGIN_URL;

    @Value( "${security.oauth2.client.clientId}" )
    private String AUTH_CLIENT_ID;

    @Value( "${security.oauth2.client.clientSecret}" )
    private String AUTH_CLIENT_PASSWORD;

    @Value( "${security.oauth2.resource.user-details-uri}" )
    private String AUTH_GET_ME_URL;

    @Autowired
    CustomLogging logger;

    public AuthServerUserDetailsDto getUserDetails(String authorization) throws Exception {

        HttpResponse<String> response = null;

        // get user details from authserver
        logger.info("calling auth server to fetch user information. token: " + authorization);
        try {
            response = Unirest.get(AUTH_GET_ME_URL).header(Constants.AUTHORIZATION, authorization)
                                                   .header(Constants.JSON_VALUE_REQUEST_ID, UUID.randomUUID().toString())
                                                   .header("correlation_id", UUID.randomUUID().toString())
                                                   .header("timestamp", String.valueOf(LocalDateTime.now()))
                                                   .header("Accept", "*/*").asString();
        } catch (Exception e) {
            return null;
        }

        logger.info("auth server responded with status: " + response.getStatus());

        // return null if user does not exists
        if (response.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
            return null;
        }

        // get user from response
        Gson gson = new Gson();
        AuthServerUserDetailsDto user = gson.fromJson(response.getBody(), AuthServerUserDetailsDto.class);

        // send user id and role
        logger.info("user fetched from auth server: " + user.getUsername());
        return user;

    }

}