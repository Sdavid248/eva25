package com.example.eva.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class NominatimService {

    private final RestTemplate rest = new RestTemplate();

    public double[] obtenerCoordenadas(String direccion) {

        try {

            String url = "https://nominatim.openstreetmap.org/search?format=json&q="
                    + direccion.replace(" ", "+");

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "eva25-app");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getBody() == null) {
                return null;
            }

            JSONArray results = new JSONArray(response.getBody());

            if (results.length() == 0) {
                return null;
            }

            JSONObject first = results.getJSONObject(0);

            double lat = Double.parseDouble(first.getString("lat"));
            double lon = Double.parseDouble(first.getString("lon"));

            return new double[]{lat, lon};

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}