package com.kafein.garage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InvalidSlotNumberException  extends RuntimeException{

    public InvalidSlotNumberException(String message) {
        super(message);
    }
}
