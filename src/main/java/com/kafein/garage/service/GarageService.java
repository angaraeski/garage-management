package com.kafein.garage.service;

import com.kafein.garage.entity.Vehicle;

public interface GarageService {

    String parkVehicle(Vehicle vehicle);

    String leaveVehicle(Integer vehicleParkNumber);

    String getStatus();
}
