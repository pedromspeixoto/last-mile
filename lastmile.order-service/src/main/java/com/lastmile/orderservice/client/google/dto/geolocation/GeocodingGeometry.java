package com.lastmile.orderservice.client.google.dto.geolocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingGeometry {

    @SerializedName("location")
    @Expose
    private GeocodingCoordinates location;

    @SerializedName("location_type")
    @Expose
    private String locationType;

    @SerializedName("viewport")
    @Expose
    private GeocodingViewport viewport;

    @SerializedName("bounds")
    @Expose
    private GeocodingViewport bounds;

    public GeocodingCoordinates getLocation() {
        return this.location;
    }

    public void setLocation(GeocodingCoordinates location) {
        this.location = location;
    }

    public String getLocationType() {
        return this.locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public GeocodingViewport getViewport() {
        return this.viewport;
    }

    public void setViewport(GeocodingViewport viewport) {
        this.viewport = viewport;
    }

    public GeocodingViewport getBounds() {
        return this.bounds;
    }

    public void setBounds(GeocodingViewport bounds) {
        this.bounds = bounds;
    }

}
