package com.example.weatherforecast.service;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ForecastResponseDTO {
    private String currentTemperature;
    private List<ForecastDay> forecastDays;
}
