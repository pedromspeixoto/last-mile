package com.lastmile.orderservice.dto;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.orderservice.enums.OrderPriority;
import com.lastmile.orderservice.enums.OrderType;
import com.lastmile.orderservice.enums.RequesterEntityType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonInclude(Include.NON_NULL)
public class EstimateRequestDto {

    private String requesterIdentification;

    private RequesterEntityType requesterEntityType;

    @NotNull(message = "Pickup latitude is mandatory")
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is mandatory")
    private Double pickupLongitude;

    @NotNull(message = "Destination latitude is mandatory")
    private Double destinationLatitude;

    @NotNull(message = "Destination longitude is mandatory")
    private Double destinationLongitude;

    @NotNull(message = "Order priority is mandatory")
    private OrderPriority priority;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private Date scheduledDate;

    @NotNull(message = "Order value is mandatory")
    private Double orderValue;

    @NotNull(message = "Order type is mandatory")
    private OrderType orderType;

    public String getRequesterIdentification() {
        return this.requesterIdentification;
    }

    public void setRequesterIdentification(String requesterIdentification) {
        this.requesterIdentification = requesterIdentification;
    }

    public RequesterEntityType getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(RequesterEntityType requesterEntityType) {
        this.requesterEntityType = requesterEntityType;
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

    public Date getScheduledDate() {
        return this.scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
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

    public EstimateRequestDto() {
    }

    public EstimateRequestDto(String requesterIdentification,
                              RequesterEntityType requesterEntityType,
                              Double pickupLatitude,
                              Double pickupLongitude,
                              Double destinationLatitude,
                              Double destinationLongitude,
                              OrderPriority priority,
                              Date scheduledDate,
                              Double orderValue,
                              OrderType orderType) {
        if (null != requesterIdentification) this.requesterIdentification = requesterIdentification;
        if (null != requesterEntityType) this.requesterEntityType = requesterEntityType;
        if (null != pickupLatitude) this.pickupLatitude = pickupLatitude;
        if (null != pickupLongitude) this.pickupLongitude = pickupLongitude;
        if (null != destinationLatitude) this.destinationLatitude = destinationLatitude;
        if (null != destinationLongitude) this.destinationLongitude = destinationLongitude;
        if (null != priority) this.priority = priority;
        if (null != scheduledDate) this.scheduledDate = scheduledDate;
        if (null != orderValue) this.orderValue = orderValue;
        if (null != orderType) this.orderType = orderType;
    }

	public Object invokeGetter(String variableName) {
        Object value = null;
		try {
			PropertyDescriptor pd = new PropertyDescriptor(variableName, this.getClass());
			Method getter = pd.getReadMethod();
			value = getter.invoke(this);
			System.out.println(value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
            e.printStackTrace();
            return null;
        }
        return value;
	}

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}