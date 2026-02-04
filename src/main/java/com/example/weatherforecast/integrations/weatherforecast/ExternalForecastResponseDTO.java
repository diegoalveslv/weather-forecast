package com.example.weatherforecast.integrations.weatherforecast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalForecastResponseDTO(
        @JsonProperty("daily_units") DailyUnits dailyUnits,
        Daily daily,
        @JsonProperty("current_units") CurrentUnits currentUnits,
        Current current
) {
}
