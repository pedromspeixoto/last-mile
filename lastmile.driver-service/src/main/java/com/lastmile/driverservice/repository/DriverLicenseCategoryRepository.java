package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.DriverLicenseCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverLicenseCategoryRepository extends JpaRepository<DriverLicenseCategory, String> {

    Optional<DriverLicenseCategory> findByDriverLicenseCategoryIdentification(String driverLicenseCategoryIdentification);

    List<DriverLicenseCategory> findByDriverLicenseIdentification(String driverLicenseIdentification);

}