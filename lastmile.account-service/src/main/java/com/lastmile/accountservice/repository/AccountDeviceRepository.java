package com.lastmile.accountservice.repository;

import com.lastmile.accountservice.domain.AccountDevice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountDeviceRepository extends JpaRepository<AccountDevice, String> {

    Optional<AccountDevice> findByUserIdentification(String userIdentification);

    Optional<AccountDevice> findByUserIdentificationAndExternalEntity(String userIdentification, String externalEntity);

}