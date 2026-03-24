package org.airtribe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ParkingFloor extends AbstractBase{
    @OneToMany(mappedBy = "floorNo", cascade = CascadeType.ALL)
    private List<ParkingSlot> parkingSlotList = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    private ParkingLot parkingLot;

    public ParkingFloor() {}

    public List<ParkingSlot> getParkingSlotList() {
        return parkingSlotList;
    }

    public void setParkingSlotList(List<ParkingSlot> parkingSlotList) {
        this.parkingSlotList = parkingSlotList;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }
}
