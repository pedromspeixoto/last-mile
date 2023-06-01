package com.lastmile.orderengine.client.orders;

import java.util.Date;
import java.util.List;

import com.lastmile.orderengine.client.orders.feign.OrderServiceFeignClient;
import com.lastmile.orderengine.dto.controller.OrderHistoryResponse;
import com.lastmile.orderengine.dto.orders.OrderHistoryResponseDto;
import com.lastmile.orderengine.service.exception.FeignCommunicationException;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;

@Component
public class OrdersBridge {

    @Autowired
    OrderServiceFeignClient orderServiceFeignClient;

    public List<OrderHistoryResponseDto> getOrders(String orderIdentification, Date startDate, Date endDate, String driverIdentification, ServiceContext serviceContext) throws FeignCommunicationException {
        List<OrderHistoryResponseDto> ordersHistoryListResponse;

        try {

            ResponseEntity<OrderHistoryResponse> orderResponse = orderServiceFeignClient.getOrders(serviceContext.getCorrelationId(),
                                                                                                   serviceContext.getUserId(),
                                                                                                   serviceContext.getPermissions(),
                                                                                                   serviceContext.getRequestId(),
                                                                                                   Constants.REQUEST_ORIGIN_INTERNAL,
                                                                                                   Constants.REQUEST_ORIGIN_ENTITY_ORDER,
                                                                                                   orderIdentification,
                                                                                                   orderIdentification,
                                                                                                   startDate,
                                                                                                   endDate,
                                                                                                   driverIdentification);
            ordersHistoryListResponse = orderResponse.getBody().getData();
        } catch (FeignException ex) {
            throw new FeignCommunicationException(ex.getMessage(), ex);
        }

        return ordersHistoryListResponse;
    }

}