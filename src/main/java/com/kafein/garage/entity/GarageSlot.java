package com.kafein.garage.entity;

import com.kafein.garage.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "GARAGE_SLOT")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarageSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int slotNumber;
    private boolean isOccupied;
    @ManyToOne
    private Vehicle vehicle;
}
