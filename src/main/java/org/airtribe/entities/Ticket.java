package org.airtribe.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

@Entity
public class Ticket extends AbstractBase{

    @ManyToOne(cascade = CascadeType.MERGE)
    private ParkingSlot slot;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Vehicle vehicle;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL)
    private Bill bill;

    private boolean isActive = true;

    public Ticket() {
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public void setSlot(ParkingSlot slot) {
        this.slot = slot;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
