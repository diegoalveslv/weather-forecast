package com.example.weatherforecast.controller;

import java.time.Instant;

record ErrorDTO(Instant timestamp, String message) {
}
