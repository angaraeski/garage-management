package com.kafein.garage.service.implementation;

import com.kafein.garage.entity.GarageSlot;
import com.kafein.garage.entity.Vehicle;
import com.kafein.garage.entity.VehicleType;
import com.kafein.garage.exception.*;
import com.kafein.garage.repository.GarageSlotRepository;
import com.kafein.garage.repository.VehicleRepository;
import com.kafein.garage.repository.VehicleTypeRepository;
import com.kafein.garage.service.GarageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GarageServiceImpl implements GarageService {

    private final VehicleRepository vehicleRepository;
    private final GarageSlotRepository garageSlotRepository;
    private final VehicleTypeRepository vehicleTypeRepository;


    @Override
    @Transactional
    public String parkVehicle(Vehicle vehicle) {

        var vehicleType = validateVehicle(vehicle);

        int requiredSlot = vehicleType.get().getRequiredSlots();
        var vehicleLocatedSlots = new ArrayList<GarageSlot>();
        var emptySlots = garageSlotRepository.findByIsOccupiedFalseOrderBySlotNumberAsc();

        if (requiredSlot >= emptySlots.size()) {
            throw new GarageFullException("Garage is full.");
        }

        vehicle.setVehicleType(vehicleType.get());
        vehicleRepository.save(vehicle);

        allocateSlot(vehicle, requiredSlot, vehicleLocatedSlots, emptySlots);

        return "Allocated " + (vehicleLocatedSlots.size()) + " slot(s).";
    }

    private void allocateSlot(Vehicle vehicle, int requiredSlot, List<GarageSlot> vehicleLocatedSlots, List<GarageSlot> emptySlots) {
        for (int i = 0; i < emptySlots.size(); i++) {
            //check if slots are sequential
            if (i == 0 || (emptySlots.get(i - 1).getSlotNumber() + 1 == emptySlots.get(i).getSlotNumber() && emptySlots.get(i + 1).getSlotNumber() - 1 == emptySlots.get(i).getSlotNumber())) {
                emptySlots.get(i).setVehicle(vehicle);
                emptySlots.get(i).setOccupied(true);
                garageSlotRepository.save(emptySlots.get(i));
                vehicleLocatedSlots.add(emptySlots.get(i));
                if (i == requiredSlot - 1 && i < 10) {
                    emptySlots.get(i + 1).setOccupied(true);
                    garageSlotRepository.save(emptySlots.get(i + 1));
                    break;
                }
            } else {
                throw new VehicleParkException("Vehicle can't park here!");
            }
        }
    }

    private Optional<VehicleType> validateVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleType() == null || vehicle.getPlateNumber() == null) {
            throw new InvalidVehicleException("Invalid vehicle");
        }

        var vehicleEntity = vehicleRepository.findByPlateNumber(vehicle.getPlateNumber());

        if (vehicleEntity.isPresent()) {
            throw new InvalidVehicleException("The car is already in the garage");
        }

        var vehicleType = vehicleTypeRepository.findByTypeName(vehicle.getVehicleType().getTypeName());

        if (vehicleType.isEmpty()) {
            throw new InvalidVehicleTypeException("Invalid vehicle type: " + vehicle.getVehicleType().getTypeName());
        }
        return vehicleType;
    }

    @Override
    @Transactional
    public String leaveVehicle(Integer vehicleParkNumber) {
        var occupiedSlotList = garageSlotRepository.findDistinctVehicleIds();
        if (occupiedSlotList.isEmpty()) {
            throw new GarageIsEmptyException("Garage is empty");
        }
        if (occupiedSlotList.size() < vehicleParkNumber || vehicleParkNumber <= 0 ) {
            throw new InvalidVehicleParkNumberException("Vehicle Park Number is not valid: " + vehicleParkNumber);
        }
        var vehicle = vehicleRepository.findById(Objects.requireNonNull(occupiedSlotList.get(vehicleParkNumber - 1)));

        if (vehicle.isEmpty()) {
            throw new InvalidVehicleException("Invalid vehicle");
        }

        var vehicleOccupiedSlots = garageSlotRepository.findByVehicle(vehicle.get());

        removeGaps(vehicleOccupiedSlots);
        vehicleOccupiedSlots.forEach(garageSlot -> {
            garageSlot.setOccupied(false);
            garageSlot.setVehicle(null);
            garageSlotRepository.save(garageSlot);

            log.info("Slot " + garageSlot.getId() + " has been vacated");
        });

        vehicleRepository.delete(vehicle.get());

        return "Vehicle %s-%s-%s has been vacated".formatted(vehicle.get().getColor(), vehicle.get().getPlateNumber(), vehicle.get().getVehicleType().getTypeName());
    }

    private void removeGaps(List<GarageSlot> garageSlotList) {
        if (garageSlotList.get(garageSlotList.size() - 1).getSlotNumber() < 10) {
            removeGap(garageSlotList.get(garageSlotList.size() - 1).getSlotNumber() + 1);//next gap
        }
    }

    private void removeGap(Integer slotNumber) {
        garageSlotRepository.findBySlotNumber(slotNumber).
                ifPresent(s -> {
                    s.setOccupied(false);
                    garageSlotRepository.save(s);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public String getStatus() {
        var occupiedSlots = garageSlotRepository.findByIsOccupiedTrueOrderBySlotNumberAsc();

        if (occupiedSlots.isEmpty()) {
            return "Garage is empty";
        }

        var vehicleSlotsMap = occupiedSlots.stream()
                .filter(slot -> slot.getVehicle() != null)
                .collect(Collectors.groupingBy(
                        slot -> getVehicleInfo(slot.getVehicle()),
                        LinkedHashMap::new,
                        Collectors.mapping(GarageSlot::getSlotNumber, Collectors.toList())
                ));

        var statusBuilder = new StringBuilder("Status:\n");
        vehicleSlotsMap.forEach((vehicleInfo, slots) ->
                statusBuilder.append(vehicleInfo).append(" [")
                        .append(slots.stream().map(Object::toString).collect(Collectors.joining(",")))
                        .append("]\n"));

        log.info("Status retrieved successfully");
        return statusBuilder.toString();

    }

    private String getVehicleInfo(Vehicle vehicle) {
        return vehicle.getPlateNumber() + " " + vehicle.getColor() + " " + vehicle.getVehicleType().getTypeName();
    }


}
