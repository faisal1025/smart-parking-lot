package org.airtribe.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.airtribe.dto.VehicleDto;
import org.airtribe.entities.ParkingFloor;
import org.airtribe.entities.ParkingLot;
import org.airtribe.entities.ParkingSlot;
import org.airtribe.entities.VehicleType;
import org.airtribe.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EntryExitIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ParkingLotRepository parkingLotRepository;
    @Autowired private ParkingFloorRepository parkingFloorRepository;
    @Autowired private ParkingSlotRepository parkingSlotRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private VehicleRepository vehicleRepository;

    @BeforeEach
    void cleanAndSeed() {
        // Clean in dependency order.
        ticketRepository.deleteAll();
        vehicleRepository.deleteAll();
        parkingSlotRepository.deleteAll();
        parkingFloorRepository.deleteAll();
        parkingLotRepository.deleteAll();

        // Seed one free slot so /entry can allocate it.
        ParkingLot lot = new ParkingLot();
        ParkingLot savedLot = parkingLotRepository.save(lot);
        ParkingFloor floor = new ParkingFloor();
        floor.setParkingLot(savedLot);
        ParkingFloor savedFloor = parkingFloorRepository.save(floor);
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotType(VehicleType.CAR);
        slot.setOccupied(false);
        slot.setFloorNo(savedFloor);
        parkingSlotRepository.save(slot);
    }

    @Test
    void entry_then_exit_happy_path() throws Exception {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleType("CAR");
        vehicleDto.setVehicleNo("KA01AB1234");

        mockMvc.perform(
                        post("/entry")
                                .param("strategy", "NEAREST")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(vehicleDto))
                )
                .andExpect(status().isCreated());

        assertThat(ticketRepository.count()).isEqualTo(1);
        assertThat(vehicleRepository.findByVehicleNo("KA01AB1234")).isPresent();
        assertThat(parkingSlotRepository.findAll())
                .hasSize(1)
                .allSatisfy(ps -> assertThat(ps.isOccupied()).isTrue());

        mockMvc.perform(
                        post("/exit")
                                .param("strategy", "HOURLY")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(vehicleDto))
                )
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cost").isNumber());

        // After exit, ticket becomes inactive and slot is freed.
        assertThat(ticketRepository.findAll())
                .hasSize(1)
                .allSatisfy(t -> assertThat(t.isActive()).isFalse());

        assertThat(parkingSlotRepository.findAll())
                .hasSize(1)
                .allSatisfy(ps -> assertThat(ps.isOccupied()).isFalse());
    }

    @Test
    void entry_should_return_202_when_no_slot_available() throws Exception {
        // remove the seed slot
        parkingSlotRepository.deleteAll();

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleType("CAR");
        vehicleDto.setVehicleNo("KA02CD9999");

        mockMvc.perform(
                        post("/entry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(vehicleDto))
                )
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("No any slot available")));

        assertThat(ticketRepository.count()).isZero();
    }

    @Test
    void exit_should_return_404_when_vehicle_not_found() throws Exception {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleType("CAR");
        vehicleDto.setVehicleNo("NOT_PRESENT");

        mockMvc.perform(
                        post("/exit")
                                .param("strategy", "HOURLY")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(vehicleDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("vehicle not found")));
    }
}
