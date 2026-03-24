package org.airtribe.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VehicleType {
    BIKE("Bike"),
    CAR("Car"),
    TRUCK("Truck");

    private String name;

    private VehicleType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static VehicleType fromString(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        // Accept either the display name (e.g. "Bike") or the enum name (e.g. "BIKE")
        for (VehicleType v : VehicleType.values()) {
            if (v.name.equalsIgnoreCase(normalized) || v.name().equalsIgnoreCase(normalized)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Unknown vehicle type: " + value);
    }
}
