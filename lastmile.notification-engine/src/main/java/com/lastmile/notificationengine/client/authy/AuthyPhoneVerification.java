package com.lastmile.notificationengine.client.authy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.authy.AuthyApiClient;
import com.authy.AuthyException;
import com.authy.api.Params;
import com.authy.api.Verification;
import com.lastmile.notificationengine.service.exception.PhoneVerificationException;

@Service
public class AuthyPhoneVerification {

    private static final String CODE_LENGTH_KEY = "code_length";
    private static final String DEFAULT_CODE_LENGTH = "4";

    private static final String PHONE_VERIFICATION_PROTOCOL = "SMS";

    @Autowired
    private AuthyApiClient authyApiClient;

    public void requestToken(String countryCode, String phoneNumber, String tokenLength) throws PhoneVerificationException {

        Params params = new Params();
        String codeLength = DEFAULT_CODE_LENGTH;

        if (tokenLength != null && !tokenLength.trim().isEmpty()) {
            codeLength = tokenLength;
        }

        params.setAttribute(CODE_LENGTH_KEY, codeLength);

        try {

            Verification verification = authyApiClient.getPhoneVerification().start(phoneNumber, countryCode,
                PHONE_VERIFICATION_PROTOCOL, params);

            if (!verification.isOk()) {
                throw new AuthyException(verification.getError() != null ? verification.getError().getMessage() : "");
            }

        } catch (AuthyException e) {

            throw new PhoneVerificationException(e.getMessage());

        }

    }

}