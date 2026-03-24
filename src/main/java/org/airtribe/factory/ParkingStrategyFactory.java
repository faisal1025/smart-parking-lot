package org.airtribe.factory;

import org.airtribe.strategy.parking.AllocationStrategies;
import org.airtribe.strategy.parking.ParkingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ParkingStrategyFactory {

    private final Map<String, ParkingStrategy> strategyMap;

    public ParkingStrategyFactory(List<ParkingStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        s -> s.getClass().getSimpleName(),
                        s -> s
                ));
    }

    public ParkingStrategy getStrategy(AllocationStrategies type) {
        return switch (type) {
            case NEAREST -> strategyMap.get("NearestParkingStrategy");
            case CHEAPEST -> strategyMap.get("CheapestParkingStrategy");
            default -> strategyMap.get("FirstAvailableStrategy");
        };
    }
}
