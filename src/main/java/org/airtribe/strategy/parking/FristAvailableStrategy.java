package org.airtribe.strategy.parking;

import org.airtribe.entities.ParkingSlot;
import org.airtribe.entities.Vehicle;

import java.util.List;

public class FristAvailableStrategy implements ParkingStrategy{
    @Override
    public ParkingSlot findSlot(Vehicle vehicle, List<ParkingSlot> availableSlots) {
        return availableSlots.stream().findFirst().orElse(null);
    }
}
