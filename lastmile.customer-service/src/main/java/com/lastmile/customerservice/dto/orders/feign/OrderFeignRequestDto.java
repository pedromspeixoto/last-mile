package com.lastmile.customerservice.dto.orders.feign;

import java.util.Date;

import com.lastmile.customerservice.dto.orders.OrderRequestDto;
import com.lastmile.utils.enums.orders.OrderPriority;
import com.lastmile.utils.enums.orders.OrderType;
import com.lastmile.utils.enums.orders.RequesterEntityType;

import org.modelmapper.ModelMapper;

public class OrderFeignRequestDto {

    private String orderExternalIdentification;

    private String requesterIdentification;

    private String requesterEntityName;

    private RequesterEntityType requesterEntityType;

    private String requesterName;

    private String requesterPhoneNumber;

    private String requesterEmail;

    private String requesterCountry;

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

    private Double orderValue;

    private OrderType orderType;

    private String paymentDetailsId;

    private Date scheduledDate;

    public OrderFeignRequestDto() {
    }

    public OrderFeignRequestDto(String customerIdentification) {

        this.requesterEntityType = RequesterEntityType.MARKETPLACE;
        this.requesterIdentification = customerIdentification;
        
    }

    public static OrderFeignRequestDto mapToFeignRequest(String customerIdentification,
                                                         String customerName,
                                                         String paymentDetailsId,
                                                         OrderRequestDto orderDto) {

        ModelMapper modelMapper = new ModelMapper();
        OrderFeignRequestDto orderFeignRequestDto = modelMapper.map(orderDto, OrderFeignRequestDto.class);

        orderFeignRequestDto.setRequesterEntityType(RequesterEntityType.MARKETPLACE);
        orderFeignRequestDto.setRequesterIdentification(customerIdentification);
        orderFeignRequestDto.setRequesterEntityName(customerName);
        orderFeignRequestDto.setPaymentDetailsId(paymentDetailsId);

        return orderFeignRequestDto;

    }

    public String getOrderExternalIdentification() {
        return this.orderExternalIdentification;
    }

    public void setOrderExternalIdentification(String orderExternalIdentification) {
        this.orderExternalIdentification = orderExternalIdentification;
    }

    public String getRequesterIdentification() {
        return this.requesterIdentification;
    }

    public void setRequesterIdentification(String requesterIdentification) {
        this.requesterIdentification = requesterIdentification;
    }

    public String getRequesterEntityName() {
        return this.requesterEntityName;
    }

    public void setRequesterEntityName(String requesterEntityName) {
        this.requesterEntityName = requesterEntityName;
    }

    public RequesterEntityType getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(RequesterEntityType requesterEntityType) {
        this.requesterEntityType = requesterEntityType;
    }

    public String getRequesterName() {
        return this.requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterPhoneNumber() {
        return this.requesterPhoneNumber;
    }

    public void setRequesterPhoneNumber(String requesterPhoneNumber) {
        this.requesterPhoneNumber = requesterPhoneNumber;
    }

    public String getRequesterEmail() {
        return this.requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getRequesterCountry() {
        return this.requesterCountry;
    }

    public void setRequesterCountry(String requesterCountry) {
        this.requesterCountry = requesterCountry;
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

    public Double getOrderValue() {
        return this.orderValue;
    }

    public void setOrderValue(Double orderValue) {
        this.orderValue = orderValue;
    }

    public OrderType getOrderType() {
        return this.orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getPaymentDetailsId() {
        return this.paymentDetailsId;
    }

    public void setPaymentDetailsId(String paymentDetailsId) {
        this.paymentDetailsId = paymentDetailsId;
    }

    public Date getScheduledDate() {
        return this.scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

}