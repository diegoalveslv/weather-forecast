package com.example.weatherforecast.integrations.weatherforecast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DailyUnits(
        @JsonProperty("temperature_2m_max") String maxTemp,
        @JsonProperty("temperature_2m_min") String minTemp,
        @JsonProperty("precipitation_probability_max") String precipitationProbabilityMax
) {
}
