package com.kafein.garage.repository;

import com.kafein.garage.entity.GarageSlot;
import com.kafein.garage.entity.Vehicle;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

public interface GarageSlotRepository extends JpaRepository<GarageSlot, Long> {

    Optional<GarageSlot> findBySlotNumber(int slotNumber);

    List<GarageSlot> findByIsOccupiedFalseOrderBySlotNumberAsc();

    List<GarageSlot> findByIsOccupiedTrueOrderBySlotNumberAsc();

    List<GarageSlot> findByVehicle(Vehicle vehicle);

    @Query("SELECT DISTINCT g.vehicle.id FROM GarageSlot g WHERE g.vehicle IS NOT NULL ORDER BY g.vehicle.id")
    List<Long> findDistinctVehicleIds();

}