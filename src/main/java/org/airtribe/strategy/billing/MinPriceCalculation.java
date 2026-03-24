package org.airtribe.strategy.billing;

import org.airtribe.entities.Ticket;
import org.springframework.beans.factory.annotation.Value;

public class MinPriceCalculation implements PriceCalculationStrategy{

    @Value("${per-hour-cost}")
    private double costPerHour;

    @Override
    public double calculatePrice(Ticket ticket) {
        long minutes = java.time.Duration.between(
                ticket.getEntryTime(),
                ticket.getExitTime()
        ).toMinutes();

        return minutes * (costPerHour/60);
    }
}
