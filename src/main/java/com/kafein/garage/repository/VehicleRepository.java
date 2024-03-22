package com.kafein.garage.repository;

import com.kafein.garage.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findById(Long id);

    Optional<Vehicle> findByPlateNumber(String plateNumber);

}
