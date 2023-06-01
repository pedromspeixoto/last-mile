package com.lastmile.orderservice.client.twilio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Optional;

import com.lastmile.orderservice.client.twilio.domain.TwilioPhoneNumber;
import com.lastmile.orderservice.client.twilio.repository.TwilioPhoneNumberRepository;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.rest.proxy.v1.service.Session;
import com.twilio.rest.proxy.v1.service.session.Participant;
import com.twilio.twiml.TwiML;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Number;

@Configuration
@Component
public class TwilioClient {

    @Autowired
    private TwilioProperties twilioProperties;

    @Autowired
    private TwilioPhoneNumberRepository twilioPhoneNumberRepository;

    @Bean
    public TwilioClient InitializeTwilioClient() {
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
        return this;
    }

    public TwiML startVoiceCall(String anonymousOutgoingPhoneNumber, String outgoingPhoneNumber) {
        VoiceResponse voiceResponse = new VoiceResponse.Builder()
                .dial(new Dial.Builder().number(new Number.Builder(outgoingPhoneNumber)
                                                           .build())
                                        .callerId(anonymousOutgoingPhoneNumber)
                                        .build())
                .build();

        return voiceResponse;
    }

    public String getAvailabeNumberFromPool() {
        ResourceSet<IncomingPhoneNumber> incomingPhoneNumbers =
            IncomingPhoneNumber.reader()
            .limit(20)
            .read();

        for (IncomingPhoneNumber incomingPhoneNumber: incomingPhoneNumbers) {
            Optional<TwilioPhoneNumber> optionalTwilioPhoneNumber = twilioPhoneNumberRepository.findByTwilioPhoneNumber(incomingPhoneNumber.getPhoneNumber().toString());
            if (optionalTwilioPhoneNumber.isPresent()) {
                TwilioPhoneNumber twilioPhoneNumber = optionalTwilioPhoneNumber.get();
                if (!twilioPhoneNumber.getInUse()) {
                    twilioPhoneNumber.setInUse(Boolean.TRUE);
                    return incomingPhoneNumber.getPhoneNumber().toString();
                }   
            } else {
                TwilioPhoneNumber twilioPhoneNumber = new TwilioPhoneNumber(incomingPhoneNumber.getSid(),
                                                                            incomingPhoneNumber.getPhoneNumber().toString(),
                                                                            Boolean.TRUE);
                twilioPhoneNumberRepository.save(twilioPhoneNumber);
                return incomingPhoneNumber.getPhoneNumber().toString();
            }
        }

        return null;

    }

    public void setNumberInUse(String phoneNumber, Boolean inUse) {

            Optional<TwilioPhoneNumber> optionalTwilioPhoneNumber = twilioPhoneNumberRepository.findByTwilioPhoneNumber(phoneNumber);
            if (optionalTwilioPhoneNumber.isPresent()) {
                TwilioPhoneNumber twilioPhoneNumber = optionalTwilioPhoneNumber.get();
                twilioPhoneNumber.setInUse(inUse);
                twilioPhoneNumberRepository.save(twilioPhoneNumber);
            }

    }

    public String getProxyIdentifier(String externalSessionId, String externalParicipantIdentifier) {

        Participant participant = Participant.fetcher(twilioProperties.getServiceSid(),
                                                      externalSessionId,
                                                      externalParicipantIdentifier)
                                             .fetch();

        return participant.getProxyIdentifier();

    }

    public Boolean isSessionActive(String externalSessionId) {

        Session session = Session.fetcher(twilioProperties.getServiceSid(),
                                          externalSessionId)
                                 .fetch();

        Session.Status status = session.getStatus();

        switch (status) {
            case OPEN:
            case IN_PROGRESS:
                return true;
            default:
                return false;
        }

    }

    public void reOpenSession(String externalSessionId) {

        Session.updater(twilioProperties.getServiceSid(),
                        externalSessionId)
               .setStatus(Session.Status.OPEN)
               .update();

    }

}