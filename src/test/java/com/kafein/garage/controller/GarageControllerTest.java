package com.kafein.garage.controller;

import com.kafein.garage.dto.VehicleDTO;
import com.kafein.garage.dto.mapper.VehicleMapper;
import com.kafein.garage.entity.Vehicle;
import com.kafein.garage.service.GarageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageControllerTest {

    @InjectMocks
    private GarageController garageController;
    @Mock
    private GarageService garageService;
    @Spy
    private VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);

    @Test
    void testParkCar_Success() {
        var vehicleDTO = VehicleDTO.builder().color("White").plateNumber("33-HT-1959").type("Car").build();

        String response = "Allocated 1 slot(s).";
        when(garageService.parkVehicle(any(Vehicle.class))).thenReturn(response);

        var result = garageController.parkCar(vehicleDTO);

        verify(garageService,times(1)).parkVehicle(any(Vehicle.class));
        assertEquals(response, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testLeaveCar_Success() {
        Integer vehicleParkNumber = 1;
        String response = "Vehicle Green-34-PO-1883-Jeep has been vacated";

        when(garageService.leaveVehicle(vehicleParkNumber)).thenReturn(response);

        var result = garageController.leaveCar(vehicleParkNumber);

        verify(garageService, times(1)).leaveVehicle(vehicleParkNumber);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void testGetStatus_Success() {
        String response = "Status: \nVehicle ABC123 Red Car [1,2,3]\nVehicle XYZ456 Blue Truck [4,5,6]";

        when(garageService.getStatus()).thenReturn(response);

        var result = garageController.getStatus();

        verify(garageService, times(1)).getStatus();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }
}