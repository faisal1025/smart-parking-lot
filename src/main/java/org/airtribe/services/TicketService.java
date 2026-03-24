package org.airtribe.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.airtribe.dto.VehicleDto;
import org.airtribe.entities.*;
import org.airtribe.exception.NoSlotFoundException;
import org.airtribe.factory.ParkingStrategyFactory;
import org.airtribe.factory.PriceStrategyFactory;
import org.airtribe.repository.ParkingSlotRepository;
import org.airtribe.repository.TicketRepository;
import org.airtribe.repository.VehicleRepository;
import org.airtribe.strategy.billing.PriceCalculationStrategies;
import org.airtribe.strategy.billing.PriceCalculationStrategy;
import org.airtribe.strategy.parking.AllocationStrategies;
import org.airtribe.strategy.parking.ParkingStrategy;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private ParkingSlotRepository slotRepository;

    @Autowired
    private ParkingStrategyFactory parkingFactory;

    @Autowired
    private PriceStrategyFactory priceStrategyFactory;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional
    public Ticket generateTicket(VehicleDto vehicleDto, String strategy) throws NoSlotFoundException{
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(VehicleType.valueOf(vehicleDto.getVehicleType().toUpperCase()));
        vehicle.setVehicleNo(vehicleDto.getVehicleNo());
        List<ParkingSlot> slots = slotRepository.findAvailableSlots(vehicle.getVehicleType());
        if(slots.isEmpty()){
            logger.error("No slot found for vehicle type: {}", vehicle.getVehicleType().getName());
            throw new NoSlotFoundException("No any slot available for vehicle type "+vehicle.getVehicleType().getName());
        }
        AllocationStrategies allocationStrategy = AllocationStrategies.valueOf(strategy.toUpperCase());
        ParkingStrategy parkingStrategy = parkingFactory.getStrategy(allocationStrategy);
        ParkingSlot slot = parkingStrategy.findSlot(vehicle, slots);
        slot.setOccupied(true);
        Ticket ticket = new Ticket();
        ticket.setSlot(slot);
        ticket.setVehicle(vehicle);
        Ticket savedTicket = ticketRepository.save(ticket);
        return savedTicket;
    }

    @Transactional
    public Bill calculateCost(VehicleDto vehicleDto, String strategy) {
        VehicleType type = VehicleType.valueOf(vehicleDto.getVehicleType().toUpperCase());
        String vehicleNo = vehicleDto.getVehicleNo();
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByVehicleNo(vehicleNo);
        if(optionalVehicle.isEmpty()){
            logger.error("Vehicle not found with vehicle number: {}", vehicleNo);
            throw new EntityNotFoundException("vehicle not found with vehicle number: "+ vehicleNo);
        }
        Vehicle vehicle = optionalVehicle.get();
        Optional<Ticket> optionalTicket = ticketRepository
                                        .findByIsActiveTrueAndVehicle(vehicle);
        if(optionalTicket.isEmpty()){
            throw new EntityNotFoundException("ticket not found");
        }
        Ticket ticket = optionalTicket.get();
        ticket.setEntryTime(ticket.getCreatedAt());
        ticket.setExitTime(LocalDateTime.now());

        PriceCalculationStrategies strategyType =
                PriceCalculationStrategies.valueOf(strategy);

        PriceCalculationStrategy priceCalculationStrategy =
                priceStrategyFactory.getStrategy(strategyType);

        Bill bill = new Bill(ticket,
                priceCalculationStrategy.calculatePrice(ticket));
        ticket.getSlot().setOccupied(false);
        ticket.setActive(false);
        ticket.setBill(bill);
        ticketRepository.save(ticket);
        return bill;
    }
}
