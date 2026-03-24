package org.airtribe.strategy.parking;

import org.airtribe.entities.ParkingSlot;
import org.airtribe.entities.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NearestParkingStrategy implements ParkingStrategy {

    @Override
    public ParkingSlot findSlot(Vehicle vehicle, List<ParkingSlot> availableSlots) {
        return findNearestSlot(vehicle, availableSlots);
    }

    private ParkingSlot findNearestSlot(Vehicle vehicle, List<ParkingSlot> availableSlot) {
        return availableSlot.stream()
                .findFirst().orElse(null);
    }
}
