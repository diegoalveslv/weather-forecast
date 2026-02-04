package com.example.weatherforecast.service;

public class EmptyCoordinatesException extends RuntimeException {
    public EmptyCoordinatesException(String message) {
        super(message);
    }
}
