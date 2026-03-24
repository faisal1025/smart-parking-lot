package org.airtribe.controllers;

import org.airtribe.entities.ParkingLot;
import org.airtribe.services.ParkingLotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parkingLots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping
    public ResponseEntity<ParkingLot> create(@RequestBody ParkingLot parkingLot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingLotService.create(parkingLot));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingLotService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAll() {
        return ResponseEntity.ok(parkingLotService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLot> update(@PathVariable Long id, @RequestBody ParkingLot parkingLot) {
        ParkingLot lot = parkingLotService.update(id, parkingLot);
        return ResponseEntity.ok(lot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parkingLotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

