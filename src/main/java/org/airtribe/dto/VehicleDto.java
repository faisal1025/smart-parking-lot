package org.airtribe.dto;

import org.airtribe.entities.VehicleType;

public class VehicleDto {
    private String vehicleType;
    private String vehicleNo;

    public VehicleDto() {
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }
}
