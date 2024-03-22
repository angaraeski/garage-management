package com.kafein.garage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GarageFullException extends RuntimeException {

    public GarageFullException(String message) {
        super(message);
    }
}
