package com.example.weatherforecast.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnableWireMock({
        @ConfigureWireMock(name = "coordinates", baseUrlProperties = "externalservices.coordinates.url"),
        @ConfigureWireMock(name = "forecast", baseUrlProperties = "externalservices.weatherforecast.url")
})
@SpringBootTest
class WeatherForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectWireMock("coordinates")
    private WireMockServer coordinatesServer;

    @InjectWireMock("forecast")
    private WireMockServer forecastServer;

    @Test
    public void shouldReturnBadRequestWhenZipcodeIsNoProvided() throws Exception {
        mockMvc.perform(
                        get(WeatherForecastController.FORECAST_ENDPOINT)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Required param 'zipcode' not provided."));
    }

    @Test
    public void shouldReturn422WhenCoordinatesApiReturnDoesntFindCoordinates() throws Exception {
        String zipcode = "1234123";
        String responseBody = "[]";

        mockCoordinatesSearch(zipcode, null, responseBody);

        mockMvc.perform(
                        get(WeatherForecastController.FORECAST_ENDPOINT)
                                .param("zipcode", zipcode)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.message").value(String.format("Coordinates not found for zipcode '%s'", zipcode)));

        coordinatesServer.verify(1, getRequestedFor(urlPathEqualTo("/search")));
    }

    @Test
    public void shouldRetrieveWeatherForecastWhenProvidedWithValidZipCodeAndCountry() throws Exception {
        String zipcode = "95014";
        String countryCode = "US";

        mockCoordinatesSearch(zipcode, countryCode, validZipCodeResponse);
        mockForecastWeatherSearch();

        mockMvc.perform(
                        get(WeatherForecastController.FORECAST_ENDPOINT)
                                .param("zipcode", zipcode)
                                .param("countryCode", countryCode)
                                .header("Cache-Control", "no-cache")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(header().string("X-Cache-Used", "false"))
                .andExpect(jsonPath("$.currentTemperature").value("15.9°C"))
                .andExpect(jsonPath("$.forecastDays").isArray())
                .andExpect(jsonPath("$.forecastDays[0].date").value("2026-02-03"))
                .andExpect(jsonPath("$.forecastDays[0].maxTemperature").value("21.3°C"))
                .andExpect(jsonPath("$.forecastDays[0].minTemperature").value("6.2°C"))
                .andExpect(jsonPath("$.forecastDays[0].precipitationProbability").value("0%"))
                .andExpect(jsonPath("$.forecastDays[1].date").value("2026-02-04"))
                .andExpect(jsonPath("$.forecastDays[1].maxTemperature").value("24.6°C"))
                .andExpect(jsonPath("$.forecastDays[1].minTemperature").value("8.5°C"))
                .andExpect(jsonPath("$.forecastDays[1].precipitationProbability").value("0%"))
                .andExpect(jsonPath("$.forecastDays[2].date").value("2026-02-05"))
                .andExpect(jsonPath("$.forecastDays[3].date").value("2026-02-06"))
                .andExpect(jsonPath("$.forecastDays[4].date").value("2026-02-07"))
                .andExpect(jsonPath("$.forecastDays[5].date").value("2026-02-08"))
                .andExpect(jsonPath("$.forecastDays[6].date").value("2026-02-09"))
        ;

        coordinatesServer.verify(1, getRequestedFor(urlPathEqualTo("/search")));
        forecastServer.verify(1, getRequestedFor(urlPathEqualTo("/v1/forecast")));
    }

    @Test
    public void shouldReturnFromCacheOnSubsequentCalls() throws Exception {
        String zipcode = "95014";
        String countryCode = "US";

        mockCoordinatesSearch(zipcode, countryCode, validZipCodeResponse);
        mockForecastWeatherSearch();

        MockHttpServletRequestBuilder getRequest = get(WeatherForecastController.FORECAST_ENDPOINT)
                .param("zipcode", zipcode)
                .param("countryCode", countryCode)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(
                        getRequest
                ).andExpect(status().isOk())
                .andExpect(header().string("X-Cache-Used", "false"))
                .andExpect(jsonPath("$.currentTemperature").value("15.9°C"));

        mockMvc.perform(
                        getRequest
                ).andExpect(status().isOk())
                .andExpect(header().string("X-Cache-Used", "true"))
                .andExpect(jsonPath("$.currentTemperature").value("15.9°C"));

        coordinatesServer.verify(1, getRequestedFor(urlPathEqualTo("/search")));
        forecastServer.verify(1, getRequestedFor(urlPathEqualTo("/v1/forecast")));
    }

    private void mockCoordinatesSearch(String zipcode, String countryCode, String responseBody) {
        Map<String, StringValuePattern> queryParams = new HashMap<>();
        queryParams.put("postalcode", equalTo(zipcode));
        if (countryCode != null)
            queryParams.put("country", equalTo(countryCode));
        queryParams.put("format", equalTo("jsonv2"));
        queryParams.put("limit", equalTo("1"));


        coordinatesServer.stubFor(WireMock.get(urlPathEqualTo("/search"))
                .withQueryParams(queryParams)
                .withHeader("User-Agent", equalTo("WeatherForecast69876"))
                .willReturn(okJson(responseBody)));
    }

    private void mockForecastWeatherSearch() {
        Map<String, StringValuePattern> queryParams = new HashMap<>();
        queryParams.put("latitude", equalTo(validLatitude));
        queryParams.put("longitude", equalTo(validLongitude));
        queryParams.put("timezone", equalTo("America/Sao_Paulo"));
        queryParams.put("daily", equalTo("temperature_2m_max,temperature_2m_min,precipitation_probability_max"));
        queryParams.put("current", equalTo("temperature_2m"));

        forecastServer.stubFor(WireMock.get(urlPathEqualTo("/v1/forecast"))
                .withQueryParams(queryParams)
                .willReturn(okJson(validWeatherForecastResponse)));
    }

    private final String validLatitude = "37.3178131";
    private final String validLongitude = "-122.0372337";
    private final String validZipCodeResponse = String.format("""
            [
                {
                    "place_id": 353662092,
                    "licence": "Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright",
                    "lat": %s,
                    "lon": %s,
                    "category": "place",
                    "type": "postcode",
                    "place_rank": 21,
                    "importance": 0.12000999999999995,
                    "addresstype": "postcode",
                    "name": "95014",
                    "display_name": "95014, Cupertino, Santa Clara County, California, United States",
                    "boundingbox": [
                        "37.2678131",
                        "37.3678131",
                        "-122.0872337",
                        "-121.9872337"
                    ]
                }
            ]
            """, validLatitude, validLongitude);

    private final String validWeatherForecastResponse = """
            
            {
                "latitude": 37.312122,
                "longitude": -122.0445,
                "generationtime_ms": 0.09739398956298828,
                "utc_offset_seconds": -10800,
                "timezone": "America/Sao_Paulo",
                "timezone_abbreviation": "GMT-3",
                "elevation": 82.0,
                "current_units": {
                    "time": "iso8601",
                    "interval": "seconds",
                    "temperature_2m": "°C"
                },
                "current": {
                    "time": "2026-02-03T22:30",
                    "interval": 900,
                    "temperature_2m": 15.9
                },
                "daily_units": {
                    "time": "iso8601",
                    "temperature_2m_max": "°C",
                    "temperature_2m_min": "°C",
                    "precipitation_probability_max": "%"
                },
                "daily": {
                    "time": [
                        "2026-02-03",
                        "2026-02-04",
                        "2026-02-05",
                        "2026-02-06",
                        "2026-02-07",
                        "2026-02-08",
                        "2026-02-09"
                    ],
                    "temperature_2m_max": [
                        21.3,
                        24.6,
                        20.0,
                        20.6,
                        19.3,
                        20.1,
                        14.9
                    ],
                    "temperature_2m_min": [
                        6.2,
                        8.5,
                        9.3,
                        12.5,
                        10.5,
                        10.5,
                        9.2
                    ],
                    "precipitation_probability_max": [
                        0,
                        0,
                        0,
                        2,
                        2,
                        21,
                        24
                    ]
                }
            }""";
}