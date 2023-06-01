package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.DriverVehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverVehicleRepository extends JpaRepository<DriverVehicle, String> {

    List<DriverVehicle> findByDriverIdentification(String driverIdentification);
    
    Optional<DriverVehicle> findByVehicleIdentification(String vehicleIdentification);

    Optional<DriverVehicle> findByDriverIdentificationAndVehicleActive(String driverIdentification, boolean isVehicleActive);

}