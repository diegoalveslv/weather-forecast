package com.example.weatherforecast.integrations.coordinates;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CoordinatesApi {

    private final RestClient coordinateRestClient;

    public List<Coordinates> getCoordinates(String zipcode, String countryCode) {
        return coordinateRestClient.get()
                .uri(uriBuilder ->
                    uriBuilder.path("/search")
                            .queryParam("postalcode", zipcode)
                            .queryParam("country", countryCode)
                            .queryParam("format", "jsonv2")
                            .queryParam("limit", 1)
                            .build()
                )
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
