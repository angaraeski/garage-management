package com.kafein.garage.dto.mapper;

import com.kafein.garage.dto.VehicleDTO;
import com.kafein.garage.entity.Vehicle;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface VehicleMapper {
    @Mapping(target = "plateNumber", source = "vehicleDTO.plateNumber")
    @Mapping(target = "color", source = "vehicleDTO.color")
    @Mapping(target = "vehicleType.typeName", source = "vehicleDTO.type")
    Vehicle vehicleDtoToVehicle(VehicleDTO vehicleDTO);
}
