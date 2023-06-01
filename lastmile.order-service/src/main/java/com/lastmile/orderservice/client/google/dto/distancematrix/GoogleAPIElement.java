
package com.lastmile.orderservice.client.google.dto.distancematrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleAPIElement {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("duration")
    @Expose
    private GoogleAPIDuration duration;
    @SerializedName("distance")
    @Expose
    private GoogleAPIDistance distance;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GoogleAPIDuration getDuration() {
        return duration;
    }

    public void setDuration(GoogleAPIDuration duration) {
        this.duration = duration;
    }

    public GoogleAPIDistance getDistance() {
        return distance;
    }

    public void setDistance(GoogleAPIDistance distance) {
        this.distance = distance;
    }

}
