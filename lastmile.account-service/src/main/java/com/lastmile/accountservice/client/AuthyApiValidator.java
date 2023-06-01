package com.lastmile.accountservice.client;

import com.authy.AuthyApiClient;
import com.authy.api.Verification;
import com.lastmile.utils.logs.CustomLogging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthyApiValidator {

    @Autowired
    AuthyApiClient authyApiClient;

    @Autowired
    private CustomLogging logger;

    public boolean verifyToken(String countryCode, String phoneNumber, String token) {

        Verification verification = null;

        try {

            logger.info("calling authy api client for phone number " + countryCode + phoneNumber);
            verification = authyApiClient.getPhoneVerification().check(phoneNumber, countryCode, token);
            logger.info("authyapiclient get phone verification resposne: " + verification.toString());

            if (!verification.isOk()) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }

    }

}