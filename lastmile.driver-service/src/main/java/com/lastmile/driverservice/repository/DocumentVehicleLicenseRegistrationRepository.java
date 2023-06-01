package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.DocumentVehicleLicenseRegistration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentVehicleLicenseRegistrationRepository extends JpaRepository<DocumentVehicleLicenseRegistration, String> {

    Optional<DocumentVehicleLicenseRegistration> findByVehicleLicenseRegIdentification(String vehicleLicenseRegIdentification);

    Optional<DocumentVehicleLicenseRegistration> findByVehicleIdentification(String vehicleIdentification);

}