package com.lastmile.driverservice.client.orders.feign;

import com.lastmile.driverservice.controller.response.OrderPhotoResponse;
import com.lastmile.driverservice.controller.response.OrderResponse;
import com.lastmile.driverservice.dto.orders.AssignDriverToOrderRequestDto;
import com.lastmile.driverservice.dto.orders.feign.OrderFeignUpdateRequestDto;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "order-service")
public interface OrderServiceFeignClient {

    @PatchMapping(value = "/orders/{orderIdentification}/driver", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse> assignDriverToOrder(@RequestHeader("correlation_id") String correlationId,
                                                        @RequestHeader("user_id") String userId,
                                                        @RequestHeader("permissions") String permissions,
                                                        @RequestHeader("request_id") String requestId,
                                                        @RequestHeader("request_origin") String requestOrigin,
                                                        @RequestHeader("request_entity") String requestEntity,
                                                        @RequestHeader("request_entity_id") String requestEntityId,
                                                        @RequestBody AssignDriverToOrderRequestDto assignDriverToOrderRequestDto, 
                                                        @PathVariable("orderIdentification") String orderIdentification);

    @GetMapping(value = "/orders/")
    ResponseEntity<OrderResponse> getOrders(@RequestHeader("correlation_id") String correlationId,
                                            @RequestHeader("user_id") String userId,
                                            @RequestHeader("permissions") String permissions,
                                            @RequestHeader("request_id") String requestId,
                                            @RequestHeader("request_origin") String requestOrigin,
                                            @RequestHeader("request_entity") String requestEntity,
                                            @RequestHeader("request_entity_id") String requestEntityId,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "offset", required = false) Integer offset,
                                            @RequestParam(value = "status", required = false) String status,
                                            @RequestParam(value = "orderIdentification", required = false) String orderIdentification,
                                            @RequestParam(value = "requesterIdentification", required = false) String requesterIdentification,
                                            @RequestParam(value = "assignedDriver", required = false) String assignedDriverIdentification);

    @GetMapping(value = "/orders/{orderIdentification}/pickup-photo")
    ResponseEntity<OrderPhotoResponse> getOrderPickupPhoto(@RequestHeader("correlation_id") String correlationId,
                                                           @RequestHeader("user_id") String userId,
                                                           @RequestHeader("permissions") String permissions,
                                                           @RequestHeader("request_id") String requestId,
                                                           @RequestHeader("request_origin") String requestOrigin,
                                                           @RequestHeader("request_entity") String requestEntity,
                                                           @RequestHeader("request_entity_id") String requestEntityId,
                                                           @PathVariable("orderIdentification") String orderIdentification);

    @GetMapping(value = "/orders/{orderIdentification}/delivery-photo")
    ResponseEntity<OrderPhotoResponse> getOrderDeliveryPhoto(@RequestHeader("correlation_id") String correlationId,
                                                             @RequestHeader("user_id") String userId,
                                                             @RequestHeader("permissions") String permissions,
                                                             @RequestHeader("request_id") String requestId,
                                                             @RequestHeader("request_origin") String requestOrigin,
                                                             @RequestHeader("request_entity") String requestEntity,
                                                             @RequestHeader("request_entity_id") String requestEntityId,
                                                             @PathVariable("orderIdentification") String orderIdentification);

    @PutMapping(value = "/orders/{orderIdentification}")
    ResponseEntity<SuccessResponse> updateOrder(@RequestHeader("correlation_id") String correlationId,
                                                @RequestHeader("user_id") String userId,
                                                @RequestHeader("permissions") String permissions,
                                                @RequestHeader("request_id") String requestId,
                                                @RequestHeader("request_origin") String requestOrigin,
                                                @RequestHeader("request_entity") String requestEntity,
                                                @RequestHeader("request_entity_id") String requestEntityId,
                                                @PathVariable(value = "orderIdentification") String orderIdentification,
                                                @RequestBody OrderFeignUpdateRequestDto orderFeignRequestDto);

    @PutMapping(value = "/orders/{orderIdentification}/pickup-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse> upsertOrderPickupPhoto(@RequestHeader("correlation_id") String correlationId,
                                                           @RequestHeader("user_id") String userId,
                                                           @RequestHeader("permissions") String permissions,
                                                           @RequestHeader("request_id") String requestId,
                                                           @RequestHeader("request_origin") String requestOrigin,
                                                           @RequestHeader("request_entity") String requestEntity,
                                                           @RequestHeader("request_entity_id") String requestEntityId,
                                                           @PathVariable(value = "orderIdentification") String orderIdentification,
                                                           @RequestPart(value = "pickupPhoto") MultipartFile pickupPhoto);

    @PutMapping(value = "/orders/{orderIdentification}/delivery-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse> upsertOrderDeliveryPhoto(@RequestHeader("correlation_id") String correlationId,
                                                             @RequestHeader("user_id") String userId,
                                                             @RequestHeader("permissions") String permissions,
                                                             @RequestHeader("request_id") String requestId,
                                                             @RequestHeader("request_origin") String requestOrigin,
                                                             @RequestHeader("request_entity") String requestEntity,
                                                             @RequestHeader("request_entity_id") String requestEntityId,
                                                             @PathVariable(value = "orderIdentification") String orderIdentification,
                                                             @RequestPart(value = "deliveryPhoto") MultipartFile deliveryPhoto);

}