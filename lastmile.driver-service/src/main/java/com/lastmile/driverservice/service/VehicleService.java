package com.lastmile.driverservice.service;

import java.util.List;
import java.util.Optional;

import com.lastmile.driverservice.dto.vehicles.AddVehicleRequestDto;
import com.lastmile.driverservice.dto.vehicles.AddVehicleResponseDto;
import com.lastmile.driverservice.dto.vehicles.DriverVehicleResponseDto;
import com.lastmile.driverservice.dto.vehicles.UpdateVehicleStatusRequestDto;
import com.lastmile.driverservice.dto.vehicles.ValidateVehicleRequestDto;
import com.lastmile.driverservice.dto.documents.VehicleRegistrationUpsertRequestDto;
import com.lastmile.driverservice.dto.vehicles.VehicleResponseDto;
import com.lastmile.driverservice.service.exception.DocumentNotFoundException;
import com.lastmile.driverservice.service.exception.DriverForbiddenException;
import com.lastmile.driverservice.service.exception.DriverNotFoundException;
import com.lastmile.driverservice.service.exception.EntityNotValidatedException;
import com.lastmile.driverservice.service.exception.ExternalServerException;
import com.lastmile.driverservice.service.exception.GenericException;
import com.lastmile.driverservice.service.exception.VehicleAlreadyActiveException;
import com.lastmile.driverservice.service.exception.VehicleNotFoundException;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.web.multipart.MultipartFile;

public interface VehicleService {

    // synchronize vehicles with external entity
    void refreshVehicles(ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, GenericException;

    // get vehicle information
    DriverVehicleResponseDto getVehicle(String vehicleIdentification, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, VehicleNotFoundException;

    // get vehicle information
    void updateVehicle(String vehicleIdentification, AddVehicleRequestDto addVehicleRequestDtoenericException, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, VehicleNotFoundException;

    // get all driver vehicles
    List<DriverVehicleResponseDto> getVehicles(String driverIdentification, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, DriverNotFoundException;

    // search vehicles
    List<VehicleResponseDto> searchVehicles(Optional<Integer> limit, Optional<Integer> offset,
                                            Optional<String> make, Optional<String> model, 
                                            Optional<String> category, Optional<String> year) throws GenericException;

    // add vehicle to driver
    AddVehicleResponseDto addVehicle(String driverIdentification, AddVehicleRequestDto addVehicleRequestDtoenericException, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, DriverNotFoundException;

    // remove vehicle from drivr
    void removeVehicle(String driverIdentification, String vehicleIdentification, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, VehicleNotFoundException, DriverNotFoundException;

    // update vehicle status
    void updateVehicleStatus(String vehicleIdentification, UpdateVehicleStatusRequestDto updateVehicleStatusRequestDto, ServiceContext serviceContext) throws EntityNotValidatedException, DriverForbiddenException, GenericException, VehicleNotFoundException, VehicleAlreadyActiveException;

    // validate vehicle
    void validateVehicle(String vehicleIdentification, ValidateVehicleRequestDto validateVehicleRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, GenericException, VehicleNotFoundException;

    // upsert vehicle registration license by vehicle identification
    void upsertVehicleRegistration(String vehicleIdentification, MultipartFile documentFront, MultipartFile documentBack, VehicleRegistrationUpsertRequestDto vehicleRegistrationUpsertRequestDto, ServiceContext serviceContext) throws DriverForbiddenException, ExternalServerException, VehicleNotFoundException, GenericException;

    // delete vehicle registration license by vehicle identification
    void deleteVehicleRegistration(String vehicleIdentification, ServiceContext serviceContext) throws DriverForbiddenException, DocumentNotFoundException, ExternalServerException, VehicleNotFoundException, GenericException;

}