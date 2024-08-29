package com.travel.flight.Flights.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PriceThresholdRequest {
    private Long flightId;
    private Double desiredPrice;
}