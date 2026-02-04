package com.example.weatherforecast.integrations.weatherforecast;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class WeatherForecastApiConfig {

    private final String forecastUrl;

    public WeatherForecastApiConfig(
            @Value("${externalservices.weatherforecast.url}") String forecastUrl
    ) {
        this.forecastUrl = forecastUrl;
    }

    @Bean
    public RestClient forecastRestClient() {
        return RestClient.builder()
                .baseUrl(forecastUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
                })
                .build();
    }
}
