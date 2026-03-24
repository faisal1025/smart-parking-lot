package org.airtribe.controllers;


import org.airtribe.dto.VehicleDto;
import org.airtribe.entities.Bill;
import org.airtribe.entities.Vehicle;
import org.airtribe.entities.VehicleType;
import org.airtribe.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExitController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/exit")
    public ResponseEntity<Bill> exit(@RequestBody VehicleDto vehicleDto,
                                     @RequestParam(value = "strategy", defaultValue = "HOURLY") String strategy) {
        Bill bill = ticketService.calculateCost(vehicleDto, strategy);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(bill);
    }
}
