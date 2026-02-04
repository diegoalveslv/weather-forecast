package com.example.weatherforecast.integrations.weatherforecast;

import com.example.weatherforecast.integrations.coordinates.Coordinates;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class WeatherForecastApi {

    private final RestClient forecastRestClient;

    public ExternalForecastResponseDTO getForecast(Coordinates coordinates) {
        return forecastRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/v1/forecast")
                                .queryParam("latitude", coordinates.lat())
                                .queryParam("longitude", coordinates.lon())
                                .queryParam("timezone", "America/Sao_Paulo")
                                .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                                .queryParam("current", "temperature_2m")
                                .build()
                )
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
