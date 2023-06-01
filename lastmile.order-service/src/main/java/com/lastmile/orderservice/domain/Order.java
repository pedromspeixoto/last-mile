package com.lastmile.orderservice.domain;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_identification")
    private String orderIdentification;

    @Column(name = "short_order_identification")
    private String shortOrderIdentification;

    @Column(name = "order_external_identification")
    private String orderExternalIdentification;

    @Column(name = "requester_identification")
    private String requesterIdentification;

    @Column(name = "requester_entity_name")
    private String requesterEntityName;
    
    @Column(name = "requester_entity_type")
    private String requesterEntityType;
    
    @Column(name = "requester_name")
    private String requesterName;
        
    @Column(name = "requester_phone_number")
	private String requesterPhoneNumber;
        
    @Column(name = "requester_email")
    private String requesterEmail;

    @Column(name = "requester_country")
    private String requesterCountry;
        
    @Column(name = "requester_city")
	private String requesterCity;
        
    @Column(name = "requester_zip_code")
    private String requesterZipCode;

    @Column(name = "pickup_latitude")
    private Double pickupLatitude;

    @Column(name = "pickup_longitude")
    private Double pickupLongitude;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "destination_country")
    private String destinationCountry;
        
    @Column(name = "destination_city")
    private String destinationCity;
        
    @Column(name = "destination_zip_code")
    private String destinationZipCode;
        
    @Column(name = "destination_latitude")
    private Double destinationLatitude;
        
    @Column(name = "destination_longitude")
    private Double destinationLongitude;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "pickup_photo")
    private String pickupPhoto;

    @Column(name = "delivery_photo")
    private String deliveryPhoto;

    @Column(name = "order_ext_voice_session")
    private String orderExtVoiceSession;

    @Column(name = "order_ext_message_session")
    private String orderExtMessageSession;

    @Column(name = "status")
    private String status;

    @Column(name = "priority")
    private String priority;

    @Column(name = "scheduled_date")
    private Date scheduledDate;

    @Column(name = "assigned_driver")
    private String assignedDriver;

    @Column(name = "packaging_eta")
    private Integer packagingEta;

    @Column(name = "pickup_eta")
    private Integer pickupEta;

    @Column(name = "delivery_eta")
    private Integer deliveryEta;

    @Column(name = "effective_pickup_time")
    private Integer effectivePickupTime;

    @Column(name = "effective_delivery_time")
    private Integer effectiveDeliveryTime;

    @Column(name = "estimated_distance")
    private Integer estimatedDistance;

    @Column(name = "order_value")
    private Double orderValue;

    @Column(name = "delivery_fee_value")
    private Double deliveryFeeValue;

    @Column(name = "driver_fee_value")
    private Double driverFeeValue;

    @Column(name = "order_rating")
    private Integer orderRating;

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "owner_identification")
    private String ownerIdentification;

    @Column(name = "is_order_tracking_active")
    private Boolean isOrderTrackingActive;

    @Column(name = "payment_status")
    private String paymentStatus;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderIdentification() {
        return this.orderIdentification;
    }

    public void setOrderIdentification(String orderIdentification) {
        this.orderIdentification = orderIdentification;
    }

    public String getShortOrderIdentification() {
        return this.shortOrderIdentification;
    }

    public void setShortOrderIdentification(String shortOrderIdentification) {
        this.shortOrderIdentification = shortOrderIdentification;
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

    public String getRequesterEntityType() {
        return this.requesterEntityType;
    }

    public void setRequesterEntityType(String requesterEntityType) {
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

    public String getPickupPhoto() {
        return this.pickupPhoto;
    }

    public void setPickupPhoto(String pickupPhoto) {
        this.pickupPhoto = pickupPhoto;
    }

    public String getDeliveryPhoto() {
        return this.deliveryPhoto;
    }

    public void setDeliveryPhoto(String deliveryPhoto) {
        this.deliveryPhoto = deliveryPhoto;
    }

    public String getOrderExtVoiceSession() {
        return this.orderExtVoiceSession;
    }

    public void setOrderExtVoiceSession(String orderExtVoiceSession) {
        this.orderExtVoiceSession = orderExtVoiceSession;
    }

    public String getOrderExtMessageSession() {
        return this.orderExtMessageSession;
    }

    public void setOrderExtMessageSession(String orderExtMessageSession) {
        this.orderExtMessageSession = orderExtMessageSession;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getScheduledDate() {
        return this.scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getAssignedDriver() {
        return this.assignedDriver;
    }

    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public Integer getPackagingEta() {
        return this.packagingEta;
    }

    public void setPackagingEta(Integer packagingEta) {
        this.packagingEta = packagingEta;
    }

    public Integer getPickupEta() {
        return this.pickupEta;
    }

    public void setPickupEta(Integer pickupEta) {
        this.pickupEta = pickupEta;
    }

    public Integer getDeliveryEta() {
        return this.deliveryEta;
    }

    public void setDeliveryEta(Integer deliveryEta) {
        this.deliveryEta = deliveryEta;
    }

    public Integer getEffectivePickupTime() {
        return this.effectivePickupTime;
    }

    public void setEffectivePickupTime(Integer effectivePickupTime) {
        this.effectivePickupTime = effectivePickupTime;
    }

    public Integer getEffectiveDeliveryTime() {
        return this.effectiveDeliveryTime;
    }

    public void setEffectiveDeliveryTime(Integer effectiveDeliveryTime) {
        this.effectiveDeliveryTime = effectiveDeliveryTime;
    }

    public Integer getEstimatedDistance() {
        return this.estimatedDistance;
    }

    public void setEstimatedDistance(Integer estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public Double getOrderValue() {
        return this.orderValue;
    }

    public void setOrderValue(Double orderValue) {
        this.orderValue = orderValue;
    }

    public Double getDeliveryFeeValue() {
        return this.deliveryFeeValue;
    }

    public void setDeliveryFeeValue(Double deliveryFeeValue) {
        this.deliveryFeeValue = deliveryFeeValue;
    }

    public Double getDriverFeeValue() {
        return this.driverFeeValue;
    }

    public void setDriverFeeValue(Double driverFeeValue) {
        this.driverFeeValue = driverFeeValue;
    }

    public Integer getOrderRating() {
        return this.orderRating;
    }

    public void setOrderRating(Integer orderRating) {
        this.orderRating = orderRating;
    }

    public String getOrderType() {
        return this.orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOwnerIdentification() {
        return this.ownerIdentification;
    }

    public void setOwnerIdentification(String ownerIdentification) {
        this.ownerIdentification = ownerIdentification;
    }

    public Boolean isIsOrderTrackingActive() {
        return this.isOrderTrackingActive;
    }

    public Boolean getIsOrderTrackingActive() {
        return this.isOrderTrackingActive;
    }

    public void setIsOrderTrackingActive(Boolean isOrderTrackingActive) {
        this.isOrderTrackingActive = isOrderTrackingActive;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPickupAddress() {
        return this.pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDestinationAddress() {
        return this.destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Order() {
    }

	public Object invokeGetter(String variableName) {
        Object value = null;
		try {
			PropertyDescriptor pd = new PropertyDescriptor(variableName, this.getClass());
			Method getter = pd.getReadMethod();
			value = getter.invoke(this);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
            return null;
        }
        return value;
	}

}