package com.kafein.garage.controller;

import com.kafein.garage.dto.VehicleDTO;
import com.kafein.garage.dto.mapper.VehicleMapper;
import com.kafein.garage.entity.Vehicle;
import com.kafein.garage.service.GarageService;
import com.kafein.garage.util.ApiPaths;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(ApiPaths.GarageControllerPaths.GARAGE_CTRL)
public class GarageController {

    private final GarageService garageService;
    private final VehicleMapper vehicleMapper;

    @PostMapping("/park")
    public ResponseEntity<String> parkCar(@Valid @RequestBody VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleMapper.vehicleDtoToVehicle(vehicleDTO);
        String response = garageService.parkVehicle(vehicle);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/leave")
    public ResponseEntity<String> leaveCar(@RequestParam Integer vehicleParkNumber) {
        String response = garageService.leaveVehicle(vehicleParkNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        String response = garageService.getStatus();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
