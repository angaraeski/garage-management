package com.kafein.garage.entity;

import com.kafein.garage.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "VEHICLE_TYPE")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String typeName;
    private int requiredSlots;
}
