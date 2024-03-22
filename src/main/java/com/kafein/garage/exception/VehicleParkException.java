package com.kafein.garage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VehicleParkException extends RuntimeException {

    public VehicleParkException(String message) {  super(message);}
}
