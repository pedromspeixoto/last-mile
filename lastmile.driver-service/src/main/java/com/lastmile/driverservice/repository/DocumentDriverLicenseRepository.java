package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.DocumentDriverLicense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentDriverLicenseRepository extends JpaRepository<DocumentDriverLicense, String> {

    Optional<DocumentDriverLicense> findByDriverLicenseIdentification(String driverLicenseIdentification);

    Optional<DocumentDriverLicense> findByDriverIdentification(String findByDriverIdentification);

}