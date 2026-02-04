package com.example.weatherforecast.service;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ForecastDay {
    private String date;
    private String maxTemperature;
    private String minTemperature;
    private String precipitationProbability;
}
