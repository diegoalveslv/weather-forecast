package com.example.weatherforecast.integrations.weatherforecast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Daily(
        List<String> time,
        @JsonProperty("temperature_2m_max") List<Double> maxTemp,
        @JsonProperty("temperature_2m_min") List<Double> minTemp,
        @JsonProperty("precipitation_probability_max") List<Integer> precipitationProbability
) {
}
