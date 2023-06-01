package com.lastmile.orderservice.client.twilio.repository;

import com.lastmile.orderservice.client.twilio.domain.TwilioPhoneNumber;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwilioPhoneNumberRepository extends JpaRepository<TwilioPhoneNumber, String> {

    Optional<TwilioPhoneNumber> findByTwilioPhoneNumber(String twilioPhoneNumber);

    Optional<TwilioPhoneNumber> findByTwilioPhoneNumberSid(String twilioPhoneNumber);

    Optional<TwilioPhoneNumber> findByTwilioPhoneNumberSidAndInUse(String twilioPhoneNumber, Boolean inUse);

    Optional<TwilioPhoneNumber> findByTwilioPhoneNumberAndInUse(String twilioPhoneNumber, Boolean inUse);

}