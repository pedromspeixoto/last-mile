
package com.lastmile.orderservice.client.google.dto.distancematrix;

public class GoogleEstimateResponseDto {

    private Integer eta;

    private Integer distance;

    public Integer getEta() {
        return this.eta;
    }

    public void setEta(Integer eta) {
        this.eta = eta;
    }

    public Integer getDistance() {
        return this.distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public GoogleEstimateResponseDto() {
    }

    public GoogleEstimateResponseDto(Integer eta, Integer distance) {
        this.eta = eta;
        this.distance = distance;
    }

}
