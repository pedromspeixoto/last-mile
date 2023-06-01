package com.lastmile.driverservice.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lastmile.driverservice.service.exception.ExternalServerException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ExternalVehiclesAPIMapper {

    @Value("${vehicles.api.url}")
    private String VEHICLES_API_URL;

    @Value("${vehicles.api.app-id}")
    private String VEHICLES_APP_ID;

    @Value("${vehicles.api.api-key}")
    private String VEHICLES_API_KEY;

    public JSONObject getVehicles() throws Exception {

        JSONObject data = new JSONObject();

        try {
            URL url = new URL(VEHICLES_API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-Parse-Application-Id", VEHICLES_APP_ID); // This is your app's application id
            urlConnection.setRequestProperty("X-Parse-REST-API-Key", VEHICLES_API_KEY); // This is your app's REST API key
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                data = new JSONObject(stringBuilder.toString()); // Here you have the data that you need
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            throw new ExternalServerException("parseapi.back4app.com", e.getCause());  
        }

        return data;
    }
}