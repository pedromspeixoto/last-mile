package com.lastmile.orderservice.client.google.dto.geolocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingViewport {

    @SerializedName("northeast")
    @Expose
    private GeocodingCoordinates northeast;

    @SerializedName("southwest")
    @Expose
    private GeocodingCoordinates southwest;

    public GeocodingCoordinates getNortheast() {
        return this.northeast;
    }

    public void setNortheast(GeocodingCoordinates northeast) {
        this.northeast = northeast;
    }

    public GeocodingCoordinates getSouthwest() {
        return this.southwest;
    }

    public void setSouthwest(GeocodingCoordinates southwest) {
        this.southwest = southwest;
    }

}