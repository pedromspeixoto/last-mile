package com.lastmile.driverservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.lastmile.driverservice.domain.FiscalEntityUserLink;

@Repository
public interface FiscalEntityUserLinkRepository extends JpaRepository<FiscalEntityUserLink, String> {

    List<FiscalEntityUserLink> findByUserIdentification(String userIdentification);

    List<FiscalEntityUserLink> findByFiscalEntityIdentification(String fiscalEntityIdentification);

    Optional<FiscalEntityUserLink> findByUserIdentificationAndFiscalEntityIdentification(String userIdentification, String fiscalEntityIdentification);

}