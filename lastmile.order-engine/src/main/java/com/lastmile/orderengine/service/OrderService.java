package com.lastmile.orderengine.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.lastmile.orderengine.client.drivers.DriversBridge;
import com.lastmile.orderengine.client.orders.OrdersBridge;
import com.lastmile.orderengine.config.ServiceProperties;
import com.lastmile.orderengine.dto.DriverResponseModel;
import com.lastmile.orderengine.dto.RabbitOrderModel;
import com.lastmile.orderengine.dto.orders.OrderHistoryResponseDto;
import com.lastmile.orderengine.enums.DriverStatus;
import com.lastmile.orderengine.service.exception.DriversNotFoundException;
import com.lastmile.orderengine.service.exception.FeignCommunicationException;
import com.lastmile.orderengine.service.exception.TemplateValidationException;
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class OrderService {

    @Autowired
    private DriversBridge driversBridge;

    @Autowired
    private OrdersBridge ordersBridge;

    @Autowired
    private ServiceProperties serviceProperties;

    @Autowired
    private CustomLogging logger;

    public String assignOrder(ServiceContext serviceContext, RabbitOrderModel order) throws FeignCommunicationException, IOException, TemplateValidationException,
            DriversNotFoundException {
        
        List<DriverResponseModel> driversList;

        logger.info("trying to find suitable driver for order " + order.getOrderIdentification(), serviceContext);

        try {
            driversList = driversBridge.getDriversByLocation(serviceContext, 
                                                             order.getPickupLatitude(),
                                                             order.getPickupLongitude(),
                                                             30000,
                                                             20,
                                                             DriverStatus.AVAILABLE);
        } catch (Exception ex) {
            logger.error("driver service returned an error when fetching drivers by location", serviceContext);
            throw new FeignCommunicationException(ex.getMessage(), ex.getCause());
        }

        // ensure that at least one driver was found
        if (driversList.isEmpty()) {
            logger.error("driver list returned by driver service is empty", serviceContext);
            throw new DriversNotFoundException(order.getOrderIdentification());
        }

        DriverResponseModel assignedDriver = null;
        for (DriverResponseModel driver : driversList) {
            List<OrderHistoryResponseDto> orderHistoryResponseDto = null;
            try {
                // ensure driver was not assigned to this order in the past 10 minutes
                orderHistoryResponseDto = ordersBridge.getOrders(order.getOrderIdentification(),
                                                                 new Date(System.currentTimeMillis() - serviceProperties.getDriverReassignTimeout() * 1000),
                                                                 new Date(),
                                                                 driver.getDriverIdentification(),
                                                                 serviceContext);
            } catch (Exception e) {
                logger.error("error thrown when ensuring driver was not assigned to this order in the past " + serviceProperties.getDriverReassignTimeout() + " seconds", serviceContext);
            }
            if (orderHistoryResponseDto.isEmpty()) {
                assignedDriver = driver;
                break;
            }
            logger.info("driver with id " + driver.getDriverIdentification() + " found but was not compatible for this order (" + order.getOrderIdentification() + ") - rejected/not accepted in the past", serviceContext);

        }

        // ensure that a matching driver was found
        if (assignedDriver == null) {
            logger.error("no matching driver found for this order", serviceContext);
            throw new DriversNotFoundException(order.getOrderIdentification());
        }

        // try to assign order to driver
        try {
            driversBridge.assignOrderToDriver(serviceContext, order.getOrderIdentification(), assignedDriver.getDriverIdentification());
        } catch (Exception ex) {
            logger.error("driver service return an error when assigning an order to a driver", serviceContext);
            throw new FeignCommunicationException(ex.getMessage(), ex.getCause());
        }

        logger.info("order with id " + order.getOrderIdentification() + " assigned to driver " + assignedDriver.getDriverIdentification(), serviceContext);

        return assignedDriver.getDriverIdentification();
    }

}