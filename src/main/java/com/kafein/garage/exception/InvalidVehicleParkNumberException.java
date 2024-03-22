package com.kafein.garage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidVehicleParkNumberException extends RuntimeException{

    public InvalidVehicleParkNumberException(String message) {
        super(message);
    }
}
