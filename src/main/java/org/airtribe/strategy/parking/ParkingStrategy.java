package org.airtribe.strategy.parking;

import org.airtribe.entities.ParkingSlot;
import org.airtribe.entities.Vehicle;

import java.util.List;

public interface ParkingStrategy {
    ParkingSlot findSlot(Vehicle vehicle, List<ParkingSlot> availableSlots);
}
