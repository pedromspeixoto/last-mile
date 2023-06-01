package com.lastmile.customerservice.dto.orders.feign;

import com.lastmile.customerservice.dto.orders.OrderRequestDto;
import com.lastmile.utils.enums.orders.OrderPriority;
import com.lastmile.utils.enums.orders.OrderStatus;
import org.modelmapper.ModelMapper;

public class OrderFeignUpdateRequestDto {

	private String requesterCity;
        
    private String requesterZipCode;

    private Double pickupLatitude;

    private Double pickupLongitude;

    private String destinationCountry;
        
    private String destinationCity;
        
    private String destinationZipCode;
        
    private Double destinationLatitude;
        
    private Double destinationLongitude;
                
    private OrderPriority priority;

    private OrderStatus orderStatus;

    private String assignedDriver;

    private Integer deliveryEta;

    private Double orderValue;

    private Integer rating;

    public OrderFeignUpdateRequestDto() {
    }

    public static OrderFeignUpdateRequestDto mapToFeignRequest(OrderRequestDto orderDto) {

        ModelMapper modelMapper = new ModelMapper();
        OrderFeignUpdateRequestDto orderFeignRequestDto = modelMapper.map(orderDto, OrderFeignUpdateRequestDto.class);
        return orderFeignRequestDto;

    }

    public String getRequesterCity() {
        return this.requesterCity;
    }

    public void setRequesterCity(String requesterCity) {
        this.requesterCity = requesterCity;
    }

    public String getRequesterZipCode() {
        return this.requesterZipCode;
    }

    public void setRequesterZipCode(String requesterZipCode) {
        this.requesterZipCode = requesterZipCode;
    }

    public Double getPickupLatitude() {
        return this.pickupLatitude;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public Double getPickupLongitude() {
        return this.pickupLongitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getDestinationCountry() {
        return this.destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDestinationCity() {
        return this.destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationZipCode() {
        return this.destinationZipCode;
    }

    public void setDestinationZipCode(String destinationZipCode) {
        this.destinationZipCode = destinationZipCode;
    }

    public Double getDestinationLatitude() {
        return this.destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return this.destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public OrderPriority getPriority() {
        return this.priority;
    }

    public void setPriority(OrderPriority priority) {
        this.priority = priority;
    }

    public OrderStatus getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAssignedDriver() {
        return this.assignedDriver;
    }

    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public Integer getDeliveryEta() {
        return this.deliveryEta;
    }

    public void setDeliveryEta(Integer deliveryEta) {
        this.deliveryEta = deliveryEta;
    }

    public Double getOrderValue() {
        return this.orderValue;
    }

    public void setOrderValue(Double orderValue) {
        this.orderValue = orderValue;
    }

    public Integer getRating() {
        return this.rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

}