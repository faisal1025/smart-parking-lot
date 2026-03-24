package org.airtribe.factory;

import org.airtribe.strategy.billing.PriceCalculationStrategies;
import org.airtribe.strategy.billing.PriceCalculationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PriceStrategyFactory {

    private Map<String, PriceCalculationStrategy> priceCalculationStrategies;

    public PriceStrategyFactory(List<PriceCalculationStrategy> strategies) {
        this.priceCalculationStrategies = strategies.stream().collect(
                Collectors.toMap(
                        s -> s.getClass().getSimpleName(),
                        s -> s
                )
        );
    }

    public PriceCalculationStrategy getStrategy(PriceCalculationStrategies type) {
        return switch(type) {
            case HOURLY -> this.priceCalculationStrategies.get("HourlyPriceCalculation");
            default -> this.priceCalculationStrategies.get("MinPriceCalculation");
        };
    }
}
