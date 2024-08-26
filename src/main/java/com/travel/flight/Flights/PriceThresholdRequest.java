package com.travel.flight.Flights;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PriceThresholdRequest {
    private Long flightId;
    private Double desiredPrice;
}