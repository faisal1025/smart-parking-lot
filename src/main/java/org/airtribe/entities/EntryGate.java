package org.airtribe.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class EntryGate extends AbstractBase{
    private Long gateNo;

    private String gateName;

    private boolean isActive = true;

    @ManyToOne
    private ParkingLot parkingLot;


    public EntryGate() {
    }

    public Long getGateNo() {
        return gateNo;
    }

    public void setGateNo(Long gateNo) {
        this.gateNo = gateNo;
    }
}
