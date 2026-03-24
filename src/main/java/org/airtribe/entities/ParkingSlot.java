package org.airtribe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class ParkingSlot extends AbstractBase{
    @Enumerated(EnumType.STRING)
    private VehicleType slotType;

    private boolean occupied;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    @JsonIgnore
    @NotNull
    private ParkingFloor floorNo;

    public ParkingSlot() {
    }

    public ParkingFloor getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(ParkingFloor floorNo) {
        this.floorNo = floorNo;
    }

    public VehicleType getSlotType() {
        return slotType;
    }

    public void setSlotType(VehicleType slotType) {
        this.slotType = slotType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
