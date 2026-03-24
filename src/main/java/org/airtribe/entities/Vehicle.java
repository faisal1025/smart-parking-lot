package org.airtribe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.DialectOverride;

import java.time.LocalDateTime;

@Entity
public class Vehicle extends AbstractBase{
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(unique = true, nullable = false)
    private String vehicleNo;

    public Vehicle() {
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }
}
