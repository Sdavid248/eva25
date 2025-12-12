package com.example.eva.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class NominatimService {

    private final RestTemplate rest = new RestTemplate();

    public double[] obtenerCoordenadas(String direccion) {

        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" +
                    direccion.replace(" ", "+");

            String response = rest.getForObject(url, String.class);

            JSONArray results = new JSONArray(response);

            if (results.length() == 0) {
                return null;
            }

            JSONObject first = results.getJSONObject(0);

            double lat = first.getDouble("lat");
            double lon = first.getDouble("lon");

            return new double[]{lat, lon};

        } catch (Exception e) {
            return null;
        }
    }
}
