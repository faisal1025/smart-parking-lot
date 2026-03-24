package org.airtribe.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.airtribe.entities.ParkingFloor;
import org.airtribe.entities.ParkingLot;
import org.airtribe.entities.ParkingSlot;
import org.airtribe.repository.ParkingFloorRepository;
import org.airtribe.repository.ParkingLotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingFloorService {

    private final ParkingFloorRepository parkingFloorRepository;
    private final ParkingLotRepository parkingLotRepository;

    public ParkingFloorService(ParkingFloorRepository parkingFloorRepository,
                              ParkingLotRepository parkingLotRepository) {
        this.parkingFloorRepository = parkingFloorRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    @Transactional
    public ParkingFloor create(Long parkingLotId, ParkingFloor floor) {
        ParkingLot lot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new EntityNotFoundException("ParkingLot not found with id: " + parkingLotId));
        floor.setParkingLot(lot);
        ParkingFloor savedFloor = parkingFloorRepository.save(floor);
        for(ParkingSlot slot: savedFloor.getParkingSlotList()){
            slot.setFloorNo(savedFloor);
        }
        return savedFloor;
    }

    public ParkingFloor getById(Long id) {
        return parkingFloorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ParkingFloor not found with id: " + id));
    }

    public List<ParkingFloor> getAll() {
        return parkingFloorRepository.findAll();
    }

    public ParkingFloor update(Long id, ParkingFloor floor) {
        ParkingFloor existing = getById(id);
        existing.setParkingSlotList(floor.getParkingSlotList());
        return parkingFloorRepository.save(existing);
    }

    public void delete(Long id) {
        if (!parkingFloorRepository.existsById(id)) {
            throw new EntityNotFoundException("ParkingFloor not found with id: " + id);
        }
        parkingFloorRepository.deleteById(id);
    }
}

