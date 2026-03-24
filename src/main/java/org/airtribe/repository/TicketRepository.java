package org.airtribe.repository;

import org.airtribe.entities.Ticket;
import org.airtribe.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByIsActiveTrueAndVehicle(Vehicle vehicle);
}
