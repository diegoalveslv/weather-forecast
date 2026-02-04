package com.example.weatherforecast.controller;

import com.example.weatherforecast.service.CacheableResponse;
import com.example.weatherforecast.service.ForecastResponseDTO;
import com.example.weatherforecast.service.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherForecastController {

    public static final String FORECAST_ENDPOINT = "/forecast";
    private final WeatherForecastService weatherForecastService;

    @GetMapping(FORECAST_ENDPOINT)
    public ResponseEntity<?> getForecast(
            @RequestParam("zipcode") String zipcode,
            @RequestParam(value = "countryCode", required = false) String countryCode,
            @RequestHeader(value = "Cache-Control", required = false) String cacheControl
    ) {
        boolean bypassCache = cacheControl != null && cacheControl.contains("no-cache");

        CacheableResponse<ForecastResponseDTO> cacheableResponse = weatherForecastService.getForecast(zipcode, countryCode, bypassCache);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Cache-Used", cacheableResponse.returnedFromCache() + "");

        return ResponseEntity.ok().headers(headers).body(cacheableResponse.response());
    }
}
