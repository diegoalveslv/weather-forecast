package com.example.weatherforecast.integrations.weatherforecast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentUnits(
        @JsonProperty("temperature_2m") String temperature
) {
}
