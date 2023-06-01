package com.lastmile.accountservice.dto.addresses.feign;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lastmile.accountservice.dto.addresses.CreateAddressRequestDto;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.enums.addresses.AddressType;

import org.modelmapper.ModelMapper;

@JsonInclude(Include.NON_EMPTY)
public class CreateAddressFeignRequestDto {

    @NotBlank(message = "Entity Identification is mandatory")
    private String entityIdentification;

    @NotBlank(message = "Entity Type is mandatory")
    private String entityType;

    @NotBlank(message = "Address Type is mandatory")
    private AddressType addressType;

    @NotBlank(message = "Address Line 1 is mandatory")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "Street Number is mandatory")
    private String streetNumber;

    private String floor;

    @NotBlank(message = "Zip Code is mandatory")
    private String zipCode;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "Country is mandatory")
    private String country;

    private String addressNotes;
    
    public CreateAddressFeignRequestDto() {
    }

    public static CreateAddressFeignRequestDto mapToFeignRequest(String entityId, EntityType entityType, CreateAddressRequestDto createAddressRequestDto) {

        ModelMapper modelMapper = new ModelMapper();
        CreateAddressFeignRequestDto createAddressFeignRequestDto = modelMapper.map(createAddressRequestDto, CreateAddressFeignRequestDto.class);

        createAddressFeignRequestDto.setEntityType(entityType.toString());
        createAddressFeignRequestDto.setEntityIdentification(entityId);

        return createAddressFeignRequestDto;
        
    }

    public String getEntityIdentification() {
        return this.entityIdentification;
    }

    public void setEntityIdentification(String entityIdentification) {
        this.entityIdentification = entityIdentification;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public AddressType getAddressType() {
        return this.addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getStreetNumber() {
        return this.streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddressNotes() {
        return this.addressNotes;
    }

    public void setAddressNotes(String addressNotes) {
        this.addressNotes = addressNotes;
    }

}
