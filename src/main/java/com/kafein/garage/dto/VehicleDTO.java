package com.kafein.garage.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO implements Serializable {

    @NotNull(message = "Field 'plateNumber' cannot be null")
    private String plateNumber;

    @NotNull(message = "Field 'color' cannot be null")
    private String color;

    @NotNull(message = "Field 'type' cannot be null")
    private String type;
}
