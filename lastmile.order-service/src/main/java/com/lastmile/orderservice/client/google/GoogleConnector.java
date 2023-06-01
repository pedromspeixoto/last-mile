package com.lastmile.orderservice.client.google;

import com.google.gson.Gson;
import com.lastmile.orderservice.client.google.dto.distancematrix.GoogleAPIDistance;
import com.lastmile.orderservice.client.google.dto.distancematrix.GoogleAPIDuration;
import com.lastmile.orderservice.client.google.dto.distancematrix.GoogleAPIResponseDto;
import com.lastmile.orderservice.client.google.dto.distancematrix.GoogleEstimateResponseDto;
import com.lastmile.orderservice.client.google.dto.geolocation.GeocodingResponse;
import com.lastmile.orderservice.service.exception.NoEstimateAvailableException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class GoogleConnector {

    @Value("${google.api-key}")
    private String GOOGLE_API_KEY;

    // distance matrix api
    @Value("${google.distance-matrix-api.base-url-string}")
    private String GOOGLE_MATRIX_API_BASE_STRING;

    @Value("${google.distance-matrix-api.origins-url-string}")
    private String GOOGLE_MATRIX_API_ORIGINS_STRING;

    @Value("${google.distance-matrix-api.destinations-url-string}")
    private String GOOGLE_MATRIX_API_DESTINATION_STRING;

    @Value("${google.distance-matrix-api.key-url-string}")
    private String GOOGLE_MATRIX_API_KEY_STRING;

    // geolocation api
    @Value("${google.geolocation-api.base-url-string}")
    private String GOOGLE_GEO_API_BASE_STRING;

    @Value("${google.geolocation-api.latlang-url-string}")
    private String GOOGLE_GEO_API_LATLNG_STRING;

    @Value("${google.geolocation-api.key-url-string}")
    private String GOOGLE_GEO_API_KEY_STRING;

    @Value("${google.geolocation-api.result-type-url-string}")
    private String GOOGLE_GEO_RES_TYPE_STRING;

    Logger logger = LoggerFactory.getLogger(GoogleConnector.class);
    
    public GoogleEstimateResponseDto getEstimate(Double originLatitude, Double originLongitude, Double destLatitude, Double destLongitude) throws UnirestException, NoEstimateAvailableException {

        HttpResponse<String> response;
        String originLocation = String.valueOf(originLatitude) + "," + String.valueOf(originLongitude);
        String destLocation = String.valueOf(destLatitude) + "," + String.valueOf(destLongitude);

        String finalURL = GOOGLE_MATRIX_API_BASE_STRING + 
                          GOOGLE_MATRIX_API_ORIGINS_STRING + originLocation + 
                          GOOGLE_MATRIX_API_DESTINATION_STRING + destLocation + 
                          GOOGLE_MATRIX_API_KEY_STRING + GOOGLE_API_KEY;

        try {
            response = Unirest.get(finalURL)
                    .header("Content-type", "application/json")
                    .header("cache-control", "no-cache")
                    .asString();

        } catch (UnirestException e) {
            throw new UnirestException(e.getMessage());
        }

        if (response.getStatus() != HttpStatus.SC_OK) {
            throw new UnirestException("Error in unirest Google Matrix API request. Status code was " + response.getStatus());
        }

        Gson gson = new Gson();
        GoogleAPIResponseDto googleAPIModel = gson.fromJson(response.getBody(), GoogleAPIResponseDto.class);

        String estimateStatus = googleAPIModel.getRows().get(0).getElements().get(0).getStatus();

        if (!estimateStatus.equals("OK")){
            throw new NoEstimateAvailableException(estimateStatus);
        }

        GoogleAPIDuration googleAPIDuration = googleAPIModel.getRows().get(0).getElements().get(0).getDuration();
        GoogleAPIDistance googleAPIDistance = googleAPIModel.getRows().get(0).getElements().get(0).getDistance();

        return new GoogleEstimateResponseDto(googleAPIDuration.getValue(),
                                             googleAPIDistance.getValue());

    }

    public String reverseGeocoding(Double latitude, Double longitude) throws UnirestException, Exception {

        HttpResponse<String> response;
        String latLngString = String.valueOf(latitude) + "," + String.valueOf(longitude);

        String finalURL = GOOGLE_GEO_API_BASE_STRING + 
                          GOOGLE_GEO_API_LATLNG_STRING + latLngString +
                          GOOGLE_GEO_RES_TYPE_STRING +
                          GOOGLE_GEO_API_KEY_STRING + GOOGLE_API_KEY;

        try {
            response = Unirest.get(finalURL)
                    .header("Content-type", "application/json")
                    .header("cache-control", "no-cache")
                    .asString();

        } catch (UnirestException e) {
            throw new UnirestException(e.getMessage());
        }

        Gson gson = new Gson();
        GeocodingResponse geocodingResponse = gson.fromJson(response.getBody(), GeocodingResponse.class);
        
        if (!geocodingResponse.getStatus().equals("OK")) {
            throw new Exception("Error calculating reverse geo code. Status code is " + response.getStatus());
        }

        return geocodingResponse.getResults().get(0).getFormattedAddress();
    }

}