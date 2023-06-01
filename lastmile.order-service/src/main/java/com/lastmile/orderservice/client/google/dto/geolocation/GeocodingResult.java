package com.lastmile.orderservice.client.google.dto.geolocation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResult {

    @SerializedName("address_components")
    @Expose
    private List<GeocodingAddressComponent> addressComponents;

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;

    @SerializedName("geometry")
    @Expose
    private GeocodingGeometry geometry;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("plus_code")
    @Expose
    private GeocodingPlusCode plusCode;

    @SerializedName("types")
    @Expose
    private List<String> types;

    public List<GeocodingAddressComponent> getAddressComponents() {
        return this.addressComponents;
    }

    public void setAddressComponents(List<GeocodingAddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public GeocodingGeometry getGeometry() {
        return this.geometry;
    }

    public void setGeometry(GeocodingGeometry geometry) {
        this.geometry = geometry;
    }

    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public GeocodingPlusCode getPlusCode() {
        return this.plusCode;
    }

    public void setPlusCode(GeocodingPlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public List<String> getTypes() {
        return this.types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
