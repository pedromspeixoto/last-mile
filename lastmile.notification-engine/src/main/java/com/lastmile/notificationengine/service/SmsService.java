package com.lastmile.notificationengine.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lastmile.notificationengine.client.authy.AuthyPhoneVerification;
import com.lastmile.notificationengine.domain.sms.Sms;
import com.lastmile.notificationengine.service.exception.ExternalCommunicationException;
import com.lastmile.notificationengine.service.exception.PhoneVerificationException;
import com.lastmile.notificationengine.service.exception.TemplateValidationException;
import com.lastmile.utils.context.ServiceContext;

@Component
public class SmsService {

    private static final String TEXT_MESSAGE_USER_ACCOUNT_ACTIVATION = "account-activation";
    private static final String TEXT_MESSAGE_USER_PASSWORD_RECOVERY = "password-recovery";

    private static final String[] TEXT_MESSAGE_TEMPLATES = { TEXT_MESSAGE_USER_ACCOUNT_ACTIVATION, TEXT_MESSAGE_USER_PASSWORD_RECOVERY };

    private static final String USER_ACCOUNT_ACTIVATION_CODE_LENGTH_KEY = "code_length";

    private static final String ERROR_INVALID_TEXT_MESSAGE_TEMPLATE = "invalid text message template: ";

    @Autowired
    private AuthyPhoneVerification authyPhoneVerificationService;

    private final Logger logger = LoggerFactory.getLogger(SmsService.class);

    public void sendTextMessage(ServiceContext serviceContext, String templateCode, Sms textMessage)
        throws TemplateValidationException, ExternalCommunicationException {

        this.validateRequest(templateCode, textMessage);

        Map<String, Object> properties = textMessage.getProperties();

        switch (templateCode) {

            case TEXT_MESSAGE_USER_ACCOUNT_ACTIVATION:
            case TEXT_MESSAGE_USER_PASSWORD_RECOVERY:

            try {

                authyPhoneVerificationService.requestToken(textMessage.getCountryCode(), textMessage.getPhoneNumber(),
                    properties != null && properties.containsKey(USER_ACCOUNT_ACTIVATION_CODE_LENGTH_KEY)
                        ? properties.get(USER_ACCOUNT_ACTIVATION_CODE_LENGTH_KEY).toString()
                        : null);

            } catch (PhoneVerificationException e) {

                throw new ExternalCommunicationException(e.getMessage());

            }

            break;
        }

        logger.info("text message " + templateCode + " sent to " + textMessage.getFullNumber());
    }

    private void validateRequest(String templateCode, Sms textMessage) throws TemplateValidationException {

        if (!isValidTemplate(templateCode)) {
            throw new TemplateValidationException(ERROR_INVALID_TEXT_MESSAGE_TEMPLATE + templateCode);
        }

    }

    private boolean isValidTemplate(String templateCode) {

        for (String template : TEXT_MESSAGE_TEMPLATES) {

            if (template.equals(templateCode)) {
            return true;
            }
        }

        return false;
    
    }

}