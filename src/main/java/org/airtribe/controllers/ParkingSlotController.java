package org.airtribe.controllers;

import org.airtribe.entities.ParkingSlot;
import org.airtribe.entities.VehicleType;
import org.airtribe.services.ParkingSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parkingSlots")
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    public ParkingSlotController(ParkingSlotService parkingSlotService) {
        this.parkingSlotService = parkingSlotService;
    }

    @PostMapping
    public ResponseEntity<ParkingSlot> create(@RequestParam("floorId") Long floorId,
                                              @RequestParam("slotType") VehicleType slotType,
                                              @RequestParam(value = "occupied", defaultValue = "false") boolean occupied) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSlotService.create(floorId, slotType, occupied));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlot> getById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSlotService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getAll() {
        return ResponseEntity.ok(parkingSlotService.getAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ParkingSlot> update(@PathVariable Long id,
                                              @RequestParam(value = "slotType", required = false) String slotType,
                                              @RequestParam(value = "occupied", required = false) Boolean occupied) {
        return ResponseEntity.ok(parkingSlotService.update(id, VehicleType.valueOf(slotType.toUpperCase()), occupied));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parkingSlotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

