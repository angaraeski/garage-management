package com.kafein.garage.service.implementation;

import com.kafein.garage.entity.GarageSlot;
import com.kafein.garage.entity.Vehicle;
import com.kafein.garage.entity.VehicleType;
import com.kafein.garage.exception.*;
import com.kafein.garage.repository.GarageSlotRepository;
import com.kafein.garage.repository.VehicleRepository;
import com.kafein.garage.repository.VehicleTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GarageServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private GarageSlotRepository garageSlotRepository;

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @InjectMocks
    private GarageServiceImpl garageService;

    private List<GarageSlot> garageSlots;
    private Vehicle leaveVehicle;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        garageSlots = IntStream.rangeClosed(1, 10)
                .mapToObj(slotNumber -> {
                    GarageSlot slot = new GarageSlot();
                    slot.setSlotNumber(slotNumber);
                    slot.setOccupied(false);
                    return slot;
                })
                .collect(Collectors.toList());
        leaveVehicle = Vehicle.builder()
                .id(1L)
                .plateNumber("ABC123")
                .color("Red")
                .vehicleType(new VehicleType(UUID.randomUUID(), "Car", 1))
                .build();
    }

    //Park

    @Test
    void testParkVehicle_Success() {
        VehicleType vehicleType = VehicleType.builder()
                .typeName("Car")
                .requiredSlots(1)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .plateNumber("ABC123")
                .color("White")
                .vehicleType(vehicleType)
                .build();

        when(vehicleTypeRepository.findByTypeName("Car")).thenReturn(Optional.of(vehicleType));
        when(garageSlotRepository.findByIsOccupiedFalseOrderBySlotNumberAsc()).thenReturn(garageSlots);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        String result = garageService.parkVehicle(vehicle);

        assertEquals("Allocated 1 slot(s).", result);
        assertTrue(garageSlots.get(0).isOccupied());
        assertEquals(vehicle, garageSlots.get(0).getVehicle());
        verify(vehicleRepository, times(1)).save(vehicle);
        verify(garageSlotRepository, times(2)).save(garageSlots.get(0));
    }

    @Test
    void testParkVehicle_Fail_GarageFull() {
        VehicleType vehicleType = VehicleType.builder()
                .typeName("Truck")
                .requiredSlots(4)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .plateNumber("ABC123")
                .color("White")
                .vehicleType(vehicleType)
                .build();

        when(vehicleTypeRepository.findByTypeName("Truck")).thenReturn(Optional.of(vehicleType));
        when(garageSlotRepository.findByIsOccupiedFalseOrderBySlotNumberAsc()).thenReturn(Collections.emptyList());

        assertThrows(GarageFullException.class, () -> garageService.parkVehicle(vehicle));
        verify(vehicleRepository, never()).save(vehicle);
        verify(garageSlotRepository, never()).save(any(GarageSlot.class));
    }

    @Test
     void testParkVehicle_Fail_InvalidVehicle() {
        Vehicle vehicle = null;

        assertThrows(InvalidVehicleException.class, () -> garageService.parkVehicle(vehicle));
        verify(vehicleRepository, never()).save(vehicle);
        verify(garageSlotRepository, never()).save(any(GarageSlot.class));
    }

    @Test
    void testParkVehicle_Fail_AlreadyInGarage() {
        VehicleType vehicleType = VehicleType.builder()
                .typeName("Car")
                .requiredSlots(1)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .plateNumber("ABC123")
                .color("White")
                .vehicleType(vehicleType)
                .build();

        when(vehicleRepository.findByPlateNumber("ABC123")).thenReturn(Optional.of(vehicle));

        assertThrows(InvalidVehicleException.class, () -> garageService.parkVehicle(vehicle));
        verify(vehicleRepository, never()).save(vehicle);
        verify(garageSlotRepository, never()).save(any(GarageSlot.class));
    }

    @Test
    void testParkVehicle_Fail_InvalidVehicleType() {
        VehicleType vehicleType = VehicleType.builder()
                .typeName("InvalidType")
                .requiredSlots(1)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .plateNumber("ABC123")
                .color("White")
                .vehicleType(vehicleType)
                .build();

        when(vehicleRepository.findByPlateNumber("ABC123")).thenReturn(Optional.empty());
        when(vehicleTypeRepository.findByTypeName("InvalidType")).thenReturn(Optional.empty());

        assertThrows(InvalidVehicleTypeException.class, () -> garageService.parkVehicle(vehicle));
        verify(vehicleRepository, never()).save(vehicle);
        verify(garageSlotRepository, never()).save(any(GarageSlot.class));
    }

    //Leave

    @Test
    void leaveVehicle_WhenGarageIsEmpty_ShouldThrowGarageIsEmptyException() {
        when(garageSlotRepository.findDistinctVehicleIds()).thenReturn(Collections.emptyList());

        assertThrows(GarageIsEmptyException.class, () -> garageService.leaveVehicle(1));
    }

    @Test
    void leaveVehicle_WhenVehicleParkNumberIsInvalid_ShouldThrowInvalidVehicleParkNumberException() {
        when(garageSlotRepository.findDistinctVehicleIds()).thenReturn(Collections.singletonList(leaveVehicle.getId()));

        assertThrows(InvalidVehicleParkNumberException.class, () -> garageService.leaveVehicle(0));
        assertThrows(InvalidVehicleParkNumberException.class, () -> garageService.leaveVehicle(2));
    }

    @Test
    void leaveVehicle_WhenVehicleNotFound_ShouldThrowInvalidVehicleException() {
        when(garageSlotRepository.findDistinctVehicleIds()).thenReturn(Collections.singletonList(leaveVehicle.getId()));
        when(vehicleRepository.findById(leaveVehicle.getId())).thenReturn(Optional.empty());

        assertThrows(InvalidVehicleException.class, () -> garageService.leaveVehicle(1));
    }

    @Test
    void leaveVehicle_WhenValidVehicleParkNumber_ShouldVacateVehicle() {
        garageSlots.get(0).setVehicle(leaveVehicle);
        garageSlots.get(0).setOccupied(true);
        when(garageSlotRepository.findDistinctVehicleIds()).thenReturn(Collections.singletonList(leaveVehicle.getId()));
        when(vehicleRepository.findById(leaveVehicle.getId())).thenReturn(Optional.of(leaveVehicle));
        when(garageSlotRepository.findByVehicle(leaveVehicle)).thenReturn(List.of(garageSlots.get(0)));

        String result = garageService.leaveVehicle(1);

        assertFalse(garageSlots.get(0).isOccupied());
        assertNull(garageSlots.get(0).getVehicle());
        verify(garageSlotRepository, times(1)).save(garageSlots.get(0));
        verify(vehicleRepository, times(1)).delete(leaveVehicle);
        assertEquals("Vehicle Red-ABC123-Car has been vacated", result);
    }

    //Status

    @Test
    void getStatus_WhenGarageIsEmpty() {
        when(garageSlotRepository.findByIsOccupiedTrueOrderBySlotNumberAsc())
                .thenReturn(Collections.emptyList());

        String status = garageService.getStatus();

        assertEquals("Garage is empty", status);
    }

    @Test
    void getStatus_WhenGarageIsOccupied() {
        VehicleType vehicleType = VehicleType.builder()
                .typeName("Car")
                .requiredSlots(1)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .plateNumber("ABC123")
                .color("White")
                .vehicleType(vehicleType)
                .build();
        Vehicle vehicle2 = Vehicle.builder()
                .plateNumber("ABC125")
                .color("Black")
                .vehicleType(vehicleType)
                .build();
        GarageSlot occupiedSlot1 = GarageSlot.builder().slotNumber(1).vehicle(vehicle).isOccupied(true).build();
        GarageSlot occupiedSlot2 = GarageSlot.builder().slotNumber(3).vehicle(vehicle2).isOccupied(true).build();

        when(garageSlotRepository.findByIsOccupiedTrueOrderBySlotNumberAsc())
                .thenReturn(Arrays.asList(occupiedSlot1, occupiedSlot2));

        String status = garageService.getStatus();

        assertEquals("Status:\nABC123 White Car [1]\nABC125 Black Car [3]\n", status);
    }

    @Test
    void getStatus_WithMultipleVehicleTypes() {
        VehicleType vehicleType = VehicleType.builder()
                .typeName("Car")
                .requiredSlots(1)
                .build();
        VehicleType vehicleType2 = VehicleType.builder()
                .typeName("Jeep")
                .requiredSlots(2)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .plateNumber("ABC123")
                .color("White")
                .vehicleType(vehicleType)
                .build();
        Vehicle vehicle2 = Vehicle.builder()
                .plateNumber("ABC125")
                .color("Black")
                .vehicleType(vehicleType2)
                .build();

        GarageSlot occupiedSlot1 = GarageSlot.builder().slotNumber(1).vehicle(vehicle).isOccupied(true).build();
        GarageSlot occupiedSlot2 = GarageSlot.builder().slotNumber(3).vehicle(vehicle2).isOccupied(true).build();

        when(garageSlotRepository.findByIsOccupiedTrueOrderBySlotNumberAsc())
                .thenReturn(Arrays.asList(occupiedSlot1, occupiedSlot2));

        String status = garageService.getStatus();

        assertEquals("Status:\nABC123 White Car [1]\nABC125 Black Jeep [3]\n", status);
    }

}
