package com.travel.flight.Flights.DTO;

import java.util.List;
import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchQuery {
    private Optional<String> flightStart = Optional.empty();
    private Optional<String> flightDestination = Optional.empty();
    private Optional<Double> minPrice = Optional.empty();
    private Optional<Double> maxPrice = Optional.empty();
    private Optional<Integer> maxTime = Optional.empty();
    private Optional<List<String>> airlines = Optional.empty();
    private Optional<Integer> maxStops = Optional.empty();
    private Optional<Integer> stopLength = Optional.empty();
    private Optional<String> tripLength = Optional.empty();
    private Optional<Boolean> carryOnAllowed = Optional.empty();
}
