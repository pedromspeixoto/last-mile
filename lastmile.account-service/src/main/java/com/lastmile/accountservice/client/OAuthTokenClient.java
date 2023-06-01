package com.lastmile.accountservice.client;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.lastmile.accountservice.domain.models.TokenModel;
import com.lastmile.accountservice.dto.ReturnUserLoginDto;
import com.lastmile.accountservice.dto.UserLoginDto;
import com.lastmile.utils.logs.CustomLogging;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.commons.codec.binary.Base64;
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

    @Autowired
    private CustomLogging logger;
    
    public ReturnUserLoginDto login(UserLoginDto user) throws UnirestException {

        HttpResponse<String> response;
        String auth = AUTH_CLIENT_ID + ":" + AUTH_CLIENT_PASSWORD;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);

        logger.info("calling auth server to login user " + user.getUsername());

        try {
            response = Unirest.post(GET_LOGIN_URL)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Authorization",  authHeader)
                    .header("cache-control", "no-cache")
                    .field("grant_type", "password")
                    .field("username", user.getUsername())
                    .field("password", user.getPassword())
                    .asString();
        } catch (UnirestException e) {
            logger.error("error calling auth serveer. unirest error: " + e.getMessage());
            throw new UnirestException("Error in unirest authorization request");
        }

        logger.info("auth server responded with status " + response.getStatus());

        if (response.getStatus() != HttpStatus.SC_OK) {
            throw new UnirestException("Error calling authorization service");
        }

        Gson gson = new Gson();
        TokenModel token = gson.fromJson(response.getBody(), TokenModel.class);

        ReturnUserLoginDto userResponse = new ReturnUserLoginDto();
        userResponse.setUsername(user.getUsername());
        userResponse.setToken(token.getAccess_token());

        return userResponse;
    }

}