package com.example.weatherforecast.service;

public record CacheableResponse<T>(T response, boolean returnedFromCache) {
}
