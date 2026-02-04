package com.example.weatherforecast.integrations.coordinates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class CoordinatesApiConfig {

    private final String coordinatesUrl;

    public CoordinatesApiConfig(
            @Value("${externalservices.coordinates.url}") String coordinatesUrl
    ) {
        this.coordinatesUrl = coordinatesUrl;
    }

    @Bean
    public RestClient coordinateRestClient() {
        return RestClient.builder()
                .baseUrl(coordinatesUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.add("User-Agent", "WeatherForecast69876");
                })
                .build();
    }
}
