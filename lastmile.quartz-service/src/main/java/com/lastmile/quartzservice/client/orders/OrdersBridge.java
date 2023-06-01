package com.lastmile.quartzservice.client.orders;

import java.util.UUID;

import com.lastmile.quartzservice.client.orders.feign.OrderServiceFeignClient;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.logs.CustomLogging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class OrdersBridge {

    @Autowired
    OrderServiceFeignClient orderServiceFeignClient;

    @Autowired
    CustomLogging logger;

    public Boolean callAssignedOrdersBatch(String batchId) throws FeignException {

        try {
            orderServiceFeignClient.processAssignedOrders(batchId,
                                                          Constants.REQUEST_ORIGIN_QUARTZ,
                                                          null,
                                                          UUID.randomUUID().toString(),
                                                          Constants.REQUEST_ORIGIN_BATCH,
                                                          null,
                                                          null);
            return true;
        } catch (Exception e) {
            logger.error("batch id: " + batchId + ". error during orders feign execution: " + e.getMessage());
            return false;
        }
    }

    public Boolean callScheduledOrdersBatch(String batchId) throws FeignException {

        try {
            orderServiceFeignClient.processScheduledOrders(batchId,
                                                           Constants.REQUEST_ORIGIN_QUARTZ,
                                                           null,
                                                           UUID.randomUUID().toString(),
                                                           Constants.REQUEST_ORIGIN_BATCH,
                                                           null,
                                                           null);
            return true;
        } catch (Exception e) {
            logger.error("batch id: " + batchId + ". error during orders feign execution: " + e.getMessage());
            return false;
        }
    }

}