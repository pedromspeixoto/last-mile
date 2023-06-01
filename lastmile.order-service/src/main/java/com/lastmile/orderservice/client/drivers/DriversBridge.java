package com.lastmile.orderservice.client.drivers;

import java.util.List;

import com.lastmile.orderservice.client.drivers.feign.DriverResponse;
import com.lastmile.orderservice.client.drivers.feign.DriverServiceFeignClient;
import com.lastmile.orderservice.client.drivers.feign.SingleDriverResponse;
import com.lastmile.orderservice.dto.drivers.DriverResponseModel;
import com.lastmile.orderservice.service.exception.FeignCommunicationException;
import com.lastmile.utils.enums.drivers.DriverStatus;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class DriversBridge {

    @Autowired
    DriverServiceFeignClient driverServiceFeignClient;

    public List<DriverResponseModel> getDrivers(ServiceContext serviceContext, Double latitude, Double longitude, Integer radius, Integer limit, DriverStatus driverStatus) throws FeignCommunicationException {


        try {

            ResponseEntity<DriverResponse> driverResponse = driverServiceFeignClient.getAvailableDrivers(serviceContext.getCorrelationId(),
                                                                                                         serviceContext.getUserId(),
                                                                                                         serviceContext.getPermissions(),
                                                                                                         serviceContext.getRequestId(),
                                                                                                         Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                         Constants.REQUEST_ORIGIN_ENTITY_ORDER,
                                                                                                         null,
                                                                                                         latitude,
                                                                                                         longitude,
                                                                                                         radius,
                                                                                                         limit,
                                                                                                         driverStatus.toString());
            return driverResponse.getBody().getData();
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

    public DriverResponseModel getDriver(ServiceContext serviceContext, String driverIdentification, Boolean includeUserProfile) throws FeignCommunicationException {

        try {

            ResponseEntity<SingleDriverResponse> driverResponse = driverServiceFeignClient.getDriver(serviceContext.getCorrelationId(),
                                                                                                     serviceContext.getUserId(),
                                                                                                     serviceContext.getPermissions(),
                                                                                                     serviceContext.getRequestId(),
                                                                                                     Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                     Constants.REQUEST_ORIGIN_ENTITY_ORDER,
                                                                                                     null,
                                                                                                     includeUserProfile,
                                                                                                     driverIdentification);
            return driverResponse.getBody().getData();
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}