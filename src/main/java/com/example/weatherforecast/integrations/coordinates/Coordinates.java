package com.example.weatherforecast.integrations.coordinates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Coordinates(
        @JsonProperty(required = true) String lat,
        @JsonProperty(required = true) String lon
) {
}