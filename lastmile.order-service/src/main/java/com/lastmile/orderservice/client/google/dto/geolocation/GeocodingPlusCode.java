package com.lastmile.orderservice.client.google.dto.geolocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingPlusCode {

    @SerializedName("compound_code")
    @Expose
    private String compoundCode;

    @SerializedName("global_code")
    @Expose
    private String globalCode;

    public String getCompoundCode() {
        return this.compoundCode;
    }

    public void setCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
    }

    public String getGlobalCode() {
        return this.globalCode;
    }

    public void setGlobalCode(String globalCode) {
        this.globalCode = globalCode;
    }

}
