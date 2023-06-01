package com.lastmile.orderservice.client.google.dto.geolocation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponse {

    @SerializedName("plus_code")
    @Expose
    private GeocodingPlusCode plusCode;

    @SerializedName("results")
    @Expose
    private List<GeocodingResult> results;

    @SerializedName("status")
    @Expose
    private String status;

    public GeocodingPlusCode getPlusCode() {
        return this.plusCode;
    }

    public void setPlusCode(GeocodingPlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public List<GeocodingResult> getResults() {
        return this.results;
    }

    public void setResults(List<GeocodingResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}