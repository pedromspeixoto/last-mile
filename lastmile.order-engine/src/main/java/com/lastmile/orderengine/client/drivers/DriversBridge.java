package com.lastmile.orderengine.client.drivers;

import java.util.List;

import com.lastmile.orderengine.client.drivers.feign.DriverResponse;
import com.lastmile.orderengine.client.drivers.feign.DriverServiceFeignClient;
import com.lastmile.orderengine.dto.AssignOrderRequestDto;
import com.lastmile.orderengine.dto.DriverResponseModel;
import com.lastmile.orderengine.enums.DriverStatus;
import com.lastmile.orderengine.service.exception.FeignCommunicationException;
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

    public List<DriverResponseModel> getDrivers(ServiceContext serviceContext, int limit, int offset, DriverStatus driverStatus) throws FeignCommunicationException {

        try {

            ResponseEntity<DriverResponse> driverResponse = driverServiceFeignClient.getDrivers(serviceContext.getCorrelationId(),
                                                                                                serviceContext.getUserId(),
                                                                                                serviceContext.getPermissions(),
                                                                                                serviceContext.getRequestId(),
                                                                                                Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                Constants.REQUEST_ORIGIN_ENTITY_ORDER,
                                                                                                null,
                                                                                                limit,
                                                                                                offset,
                                                                                                driverStatus.toString());
            return driverResponse.getBody().getData();
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }
    }

    public List<DriverResponseModel> getDriversByLocation(ServiceContext serviceContext, Double latitude, Double longitude, Integer radius, Integer limit, DriverStatus driverStatus) throws FeignCommunicationException {

        try {

            ResponseEntity<DriverResponse> driverResponse = driverServiceFeignClient.getDriversByLocation(serviceContext.getCorrelationId(),
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

    public void assignOrderToDriver(ServiceContext serviceContext, String orderIdentification, String driverIdentification) throws FeignCommunicationException {

        AssignOrderRequestDto assignOrderRequestDto = new AssignOrderRequestDto();
        assignOrderRequestDto.setOrderIdentification(orderIdentification);

        try {
            driverServiceFeignClient.assignOrderToDriver(serviceContext.getCorrelationId(),
                                                         serviceContext.getUserId(),
                                                         serviceContext.getPermissions(),
                                                         serviceContext.getRequestId(),
                                                         Constants.REQUEST_ORIGIN_INTERNAL,
                                                         Constants.REQUEST_ORIGIN_ENTITY_ORDER,
                                                         orderIdentification,
                                                         assignOrderRequestDto,
                                                         driverIdentification);    
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

    }

}