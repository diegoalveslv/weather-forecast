package com.example.weatherforecast.controller;

import com.example.weatherforecast.service.EmptyCoordinatesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingVariable(MissingServletRequestParameterException ex) {
        String variableName = ex.getParameterName();
        String errorMessage = String.format("Required param '%s' not provided.", variableName);
        return ResponseEntity.badRequest().body(new ErrorDTO(Instant.now(), errorMessage));
    }

    @ExceptionHandler(EmptyCoordinatesException.class)
    public ResponseEntity<?> handleEmptyCoordinates(EmptyCoordinatesException ex) {
        return ResponseEntity.status(422).body(new ErrorDTO(Instant.now(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        log.error("Unexpected exception. Returning Internal Server Error", ex);
        return ResponseEntity.internalServerError().body(new ErrorDTO(Instant.now(), "Unexpected error occurred. Try again later or contact the administrador."));
    }
}
