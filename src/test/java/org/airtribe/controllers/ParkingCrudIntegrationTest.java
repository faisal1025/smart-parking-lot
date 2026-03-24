package org.airtribe.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.airtribe.entities.VehicleType;
import org.airtribe.repository.ParkingFloorRepository;
import org.airtribe.repository.ParkingLotRepository;
import org.airtribe.repository.ParkingSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ParkingCrudIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired ParkingLotRepository parkingLotRepository;
    @Autowired ParkingFloorRepository parkingFloorRepository;
    @Autowired ParkingSlotRepository parkingSlotRepository;

    @BeforeEach
    void cleanup() {
        // Dependency order: slots -> floors -> lots
        parkingSlotRepository.deleteAll();
        parkingFloorRepository.deleteAll();
        parkingLotRepository.deleteAll();
    }

    @Test
    void parkingLot_crud_happy_path() throws Exception {
        // Create
        String createResponse = mockMvc.perform(post("/parkingLots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long lotId = objectMapper.readTree(createResponse).get("id").asLong();
        assertThat(parkingLotRepository.existsById(lotId)).isTrue();

        // Read
        mockMvc.perform(get("/parkingLots/{id}", lotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lotId));

        // List
        mockMvc.perform(get("/parkingLots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Update (no-op update is fine; ensures endpoint works)
        mockMvc.perform(put("/parkingLots/{id}", lotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lotId));

        // Delete
        mockMvc.perform(delete("/parkingLots/{id}", lotId))
                .andExpect(status().isNoContent());

        assertThat(parkingLotRepository.existsById(lotId)).isFalse();
    }

    @Test
    void parkingFloor_create_requires_existing_parkingLot() throws Exception {
        mockMvc.perform(post("/parkingFloors")
                        .param("parkingLotId", "999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                // EntityNotFoundException handled by GlobalExceptionHandler -> 404
                .andExpect(status().isNotFound());
    }

    @Test
    void parkingSlot_create_requires_existing_floor() throws Exception {
        mockMvc.perform(post("/parkingSlots")
                        .param("floorId", "999999")
                        .param("slotType", VehicleType.CAR.name())
                        .param("occupied", "false"))
                .andExpect(status().isNotFound());
    }

    @Test
    void full_chain_create_lot_then_floor_then_slot_then_update_and_delete() throws Exception {
        // Create lot
        String lotJson = mockMvc.perform(post("/parkingLots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long lotId = objectMapper.readTree(lotJson).get("id").asLong();

        // Create floor under lot
        String floorJson = mockMvc.perform(post("/parkingFloors")
                        .param("parkingLotId", lotId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();
        Long floorId = objectMapper.readTree(floorJson).get("id").asLong();

        // Create slot under floor
        String slotJson = mockMvc.perform(post("/parkingSlots")
                        .param("floorId", floorId.toString())
                        .param("slotType", VehicleType.CAR.name())
                        .param("occupied", "false"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.occupied").value(false))
                .andReturn().getResponse().getContentAsString();
        Long slotId = objectMapper.readTree(slotJson).get("id").asLong();

        // Update slot
        mockMvc.perform(patch("/parkingSlots/{id}", slotId)
                        .param("occupied", "true").param("slotType", "Car"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slotId))
                .andExpect(jsonPath("$.occupied").value(true));

        // Delete slot
        mockMvc.perform(delete("/parkingSlots/{id}", slotId))
                .andExpect(status().isNoContent());

        // Delete floor
        mockMvc.perform(delete("/parkingFloors/{id}", floorId))
                .andExpect(status().isNoContent());

        // Delete lot
        mockMvc.perform(delete("/parkingLots/{id}", lotId))
                .andExpect(status().isNoContent());

        assertThat(parkingLotRepository.count()).isZero();
        assertThat(parkingFloorRepository.count()).isZero();
        assertThat(parkingSlotRepository.count()).isZero();
    }
}

