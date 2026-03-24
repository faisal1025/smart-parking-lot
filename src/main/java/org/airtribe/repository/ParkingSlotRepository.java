package org.airtribe.repository;

import jakarta.persistence.LockModeType;
import org.airtribe.entities.ParkingSlot;
import org.airtribe.entities.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.occupied = false AND ps.slotType = :type")
    List<ParkingSlot> findAvailableSlots(@Param("type") VehicleType type);
}
