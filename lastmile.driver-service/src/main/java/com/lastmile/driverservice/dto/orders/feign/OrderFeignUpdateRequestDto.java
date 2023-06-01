package com.lastmile.driverservice.dto.orders.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.driverservice.dto.orders.OrderUpdateRequestDto;
import com.lastmile.utils.enums.orders.OrderPriority;
import com.lastmile.utils.enums.orders.OrderStatus;
import com.lastmile.utils.validations.ValidCountryCode;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.modelmapper.ModelMapper;

@JsonInclude(Include.NON_NULL)
public class OrderFeignUpdateRequestDto {

	private String contactCity;
        
    private String contactZipCode;

    private Double contactLatitude;

    private Double contactLongitude;

    @ValidCountryCode
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

    public static OrderFeignUpdateRequestDto mapToFeignRequest(OrderUpdateRequestDto orderDto) {

        ModelMapper modelMapper = new ModelMapper();
        OrderFeignUpdateRequestDto orderFeignRequestDto = modelMapper.map(orderDto, OrderFeignUpdateRequestDto.class);
        return orderFeignRequestDto;

    }

    public String getContactCity() {
        return this.contactCity;
    }

    public void setContactCity(String contactCity) {
        this.contactCity = contactCity;
    }

    public String getContactZipCode() {
        return this.contactZipCode;
    }

    public void setContactZipCode(String contactZipCode) {
        this.contactZipCode = contactZipCode;
    }

    public Double getContactLatitude() {
        return this.contactLatitude;
    }

    public void setContactLatitude(Double contactLatitude) {
        this.contactLatitude = contactLatitude;
    }

    public Double getContactLongitude() {
        return this.contactLongitude;
    }

    public void setContactLongitude(Double contactLongitude) {
        this.contactLongitude = contactLongitude;
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

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}