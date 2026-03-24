package org.airtribe.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ParkingLot extends AbstractBase {
    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL)
    private List<ParkingFloor> parkingFloorList = new ArrayList<>();

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL)
    private List<EntryGate> entries = new ArrayList<>();

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL)
    private List<ExitGate> exits = new ArrayList<>();

    public ParkingLot() {}

    public List<ParkingFloor> getParkingFloorList() {
        return parkingFloorList;
    }

    public void setParkingFloorList(List<ParkingFloor> parkingFloorList) {
        this.parkingFloorList = parkingFloorList;
    }

    public List<ExitGate> getExit() {
        return exits;
    }

    public void setExit(List<ExitGate> exits) {
        this.exits = exits;
    }

    public List<EntryGate> getEntry() {
        return entries;
    }

    public void setEntry(List<EntryGate> entries) {
        this.entries = entries;
    }
}
