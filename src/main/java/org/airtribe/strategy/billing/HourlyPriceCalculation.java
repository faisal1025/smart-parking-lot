package org.airtribe.strategy.billing;

import org.airtribe.entities.Bill;
import org.airtribe.entities.Ticket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HourlyPriceCalculation implements PriceCalculationStrategy{

    private double costPerHour;

    public HourlyPriceCalculation(@Value("${per-hour-cost}") double costPerHour) {
        this.costPerHour = costPerHour;
    }

    @Override
    public double calculatePrice(Ticket ticket) {
        long hour = java.time.Duration.between(
                ticket.getCreatedAt(),
                LocalDateTime.now()
                ).toHours();
        return (this.costPerHour * hour);
    }
}
