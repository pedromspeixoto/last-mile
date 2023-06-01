package com.lastmile.orderservice.client.google.dto.geolocation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingAddressComponent {

    @SerializedName("long_name")
    @Expose
    private String longName;

    @SerializedName("short_name")
    @Expose
    private String shortName;

    @SerializedName("lat")
    @Expose
    private List<String> types;
}
