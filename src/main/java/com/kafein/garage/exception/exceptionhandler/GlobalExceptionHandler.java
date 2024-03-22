package com.kafein.garage.exception.exceptionhandler;

import com.kafein.garage.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GarageFullException.class)
    public ResponseEntity<String> handleGarageFullException(GarageFullException ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GarageIsEmptyException.class)
    public ResponseEntity<String> handleGarageIsEmptyException(GarageIsEmptyException ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidVehicleException.class)
    public ResponseEntity<String> handleInvalidVehicleException(InvalidVehicleException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidVehicleTypeException.class)
    public ResponseEntity<String> handleInvalidVehicleTypeException(InvalidVehicleTypeException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidVehicleParkNumberException.class)
    public ResponseEntity<String> handleInvalidVehicleParkNumberException(InvalidVehicleParkNumberException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidSlotNumberException.class)
    public ResponseEntity<String> handleInvalidSlotNumberException(InvalidSlotNumberException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(VehicleParkException.class)
    public ResponseEntity<String> handleVehicleParkException(VehicleParkException ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String bodyOfResponse = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.BAD_REQUEST);
    }
}
