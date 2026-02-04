package com.example.weatherforecast.service;

import com.example.weatherforecast.integrations.coordinates.Coordinates;
import com.example.weatherforecast.integrations.coordinates.CoordinatesApi;
import com.example.weatherforecast.integrations.weatherforecast.Daily;
import com.example.weatherforecast.integrations.weatherforecast.ExternalForecastResponseDTO;
import com.example.weatherforecast.integrations.weatherforecast.WeatherForecastApi;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherForecastService {

    private final CoordinatesApi coordinatesApi;
    private final WeatherForecastApi weatherForecastApi;
    private final Cache<String, ForecastResponseDTO> weatherForecastCache = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build();

    public CacheableResponse<ForecastResponseDTO> getForecast(String zipcode, String countryCode, boolean bypassCache) {
        String cacheKey = zipcode + "::" + countryCode;
        ForecastResponseDTO cachedResponse = bypassCache ? null : weatherForecastCache.getIfPresent(cacheKey);

        if (cachedResponse != null) {
            log.info("Forecast retrieved from cache for key={}", cacheKey);
            return new CacheableResponse<>(cachedResponse, true);
        } else {
            log.info("Getting forecast for zipcode={}, cacheKey={}", zipcode, cacheKey);

            List<Coordinates> coordinates = getCoordinates(zipcode, countryCode);
            Coordinates coordinatesFound = coordinates.getFirst();
            log.info("Coordinates found={}", coordinatesFound);

            ExternalForecastResponseDTO forecast = weatherForecastApi.getForecast(coordinatesFound);
            List<ForecastDay> forecastDays = getForecastDays(forecast);

            ForecastResponseDTO forecastResponseDTO = ForecastResponseDTO.builder()
                    .currentTemperature(forecast.current().temperature() + forecast.currentUnits().temperature())
                    .forecastDays(forecastDays)
                    .build();

            weatherForecastCache.put(cacheKey, forecastResponseDTO);
            return new CacheableResponse<>(forecastResponseDTO, false);
        }
    }

    private @NonNull List<Coordinates> getCoordinates(String zipcode, String countryCode) {
        List<Coordinates> coordinates = coordinatesApi.getCoordinates(zipcode, countryCode);

        if (coordinates == null || coordinates.isEmpty()) {
            String message = String.format("Coordinates not found for zipcode '%s'", zipcode);
            log.info(message);
            throw new EmptyCoordinatesException(message);
        }
        return coordinates;
    }

    @Nonnull
    private static List<ForecastDay> getForecastDays(ExternalForecastResponseDTO forecast) {
        List<ForecastDay> forecastDays = new ArrayList<>();
        Daily daily = forecast.daily();
        for (int i = 0; i < daily.time().size(); i++) {
            String tempUnit = forecast.dailyUnits().maxTemp();
            String precipitationProbUnit = forecast.dailyUnits().precipitationProbabilityMax();
            ForecastDay forecastDay = ForecastDay.builder()
                    .date(daily.time().get(i))
                    .maxTemperature(daily.maxTemp().get(i) + tempUnit)
                    .minTemperature(daily.minTemp().get(i) + tempUnit)
                    .precipitationProbability(daily.precipitationProbability().get(i) + precipitationProbUnit)
                    .build();
            forecastDays.add(forecastDay);
        }
        return forecastDays;
    }
}
