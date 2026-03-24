package org.airtribe.services;

import jakarta.persistence.EntityNotFoundException;
import org.airtribe.entities.ParkingFloor;
import org.airtribe.entities.ParkingSlot;
import org.airtribe.entities.VehicleType;
import org.airtribe.repository.ParkingFloorRepository;
import org.airtribe.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingFloorRepository parkingFloorRepository;

    public ParkingSlotService(ParkingSlotRepository parkingSlotRepository,
                              ParkingFloorRepository parkingFloorRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingFloorRepository = parkingFloorRepository;
    }

    public ParkingSlot create(Long floorId, VehicleType slotType, boolean occupied) {
        ParkingFloor floor = parkingFloorRepository.findById(floorId)
                .orElseThrow(() -> new EntityNotFoundException("ParkingFloor not found with id: " + floorId));

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotType(slotType);
        slot.setOccupied(occupied);
        slot.setFloorNo(floor);
        return parkingSlotRepository.save(slot);
    }

    public ParkingSlot create(Long floorId, VehicleType slotType) {
        ParkingFloor floor = parkingFloorRepository.findById(floorId)
                .orElseThrow(() -> new EntityNotFoundException("ParkingFloor not found with id: " + floorId));

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotType(slotType);
        slot.setOccupied(false);
        slot.setFloorNo(floor);
        return parkingSlotRepository.save(slot);
    }

    public ParkingSlot getById(Long id) {
        return parkingSlotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ParkingSlot not found with id: " + id));
    }

    public List<ParkingSlot> getAll() {
        return parkingSlotRepository.findAll();
    }

    public ParkingSlot update(Long id, VehicleType slotType, Boolean occupied) {
        ParkingSlot existing = getById(id);
        if (slotType != null) {
            existing.setSlotType(slotType);
        }
        if (occupied != null) {
            existing.setOccupied(occupied);
        }
        return parkingSlotRepository.save(existing);
    }

    public void delete(Long id) {
        if (!parkingSlotRepository.existsById(id)) {
            throw new EntityNotFoundException("ParkingSlot not found with id: " + id);
        }
        parkingSlotRepository.deleteById(id);
    }
}

