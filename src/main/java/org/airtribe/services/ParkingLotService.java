package org.airtribe.services;

import jakarta.persistence.EntityNotFoundException;
import org.airtribe.entities.ParkingLot;
import org.airtribe.repository.ParkingLotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public ParkingLot create(ParkingLot parkingLot) {
        return parkingLotRepository.save(parkingLot);
    }

    public ParkingLot getById(Long id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ParkingLot not found with id: " + id));
    }

    public List<ParkingLot> getAll() {
        return parkingLotRepository.findAll();
    }

    public ParkingLot update(Long id, ParkingLot parkingLot) {
        ParkingLot existing = getById(id);
        // Minimal update: allow updating child collections if provided.
        existing.setParkingFloorList(parkingLot.getParkingFloorList());
        existing.setEntry(parkingLot.getEntry());
        existing.setExit(parkingLot.getExit());
        return parkingLotRepository.save(existing);
    }

    public void delete(Long id) {
        if (!parkingLotRepository.existsById(id)) {
            throw new EntityNotFoundException("ParkingLot not found with id: " + id);
        }
        parkingLotRepository.deleteById(id);
    }
}

