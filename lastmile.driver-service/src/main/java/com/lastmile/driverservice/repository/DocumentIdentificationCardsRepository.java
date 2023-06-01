package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.DocumentIdentificationCard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentIdentificationCardsRepository extends JpaRepository<DocumentIdentificationCard, String> {

    Optional<DocumentIdentificationCard> findByIdentificationCardIdentification(String identificationCardIdentification);

    Optional<DocumentIdentificationCard> findByDriverIdentification(String driverIdentification);

}