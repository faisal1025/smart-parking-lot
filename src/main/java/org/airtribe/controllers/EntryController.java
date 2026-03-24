package org.airtribe.controllers;

import org.airtribe.dto.VehicleDto;
import org.airtribe.entities.Ticket;
import org.airtribe.entities.Vehicle;
import org.airtribe.exception.NoSlotFoundException;
import org.airtribe.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EntryController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/entry")
    public ResponseEntity<Ticket> entry(
            @RequestParam(value = "strategy", defaultValue = "NEAREST") String strategy,
            @RequestBody VehicleDto vehicle) throws NoSlotFoundException {
        Ticket ticket = ticketService.generateTicket(vehicle, strategy);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
