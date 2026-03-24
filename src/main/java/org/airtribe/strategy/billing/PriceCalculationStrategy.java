package org.airtribe.strategy.billing;

import org.airtribe.entities.Bill;
import org.airtribe.entities.Ticket;

public interface PriceCalculationStrategy {
    double calculatePrice(Ticket ticket);
}
