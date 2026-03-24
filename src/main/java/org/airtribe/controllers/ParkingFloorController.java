package org.airtribe.controllers;

import org.airtribe.entities.ParkingFloor;
import org.airtribe.services.ParkingFloorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parkingFloors")
public class ParkingFloorController {

    private final ParkingFloorService parkingFloorService;

    public ParkingFloorController(ParkingFloorService parkingFloorService) {
        this.parkingFloorService = parkingFloorService;
    }

    @PostMapping
    public ResponseEntity<ParkingFloor> create(@RequestParam("parkingLotId") Long parkingLotId,
                                               @RequestBody ParkingFloor floor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingFloorService.create(parkingLotId, floor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingFloor> getById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingFloorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ParkingFloor>> getAll() {
        return ResponseEntity.ok(parkingFloorService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingFloor> update(@PathVariable Long id, @RequestBody ParkingFloor floor) {
        return ResponseEntity.ok(parkingFloorService.update(id, floor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parkingFloorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

