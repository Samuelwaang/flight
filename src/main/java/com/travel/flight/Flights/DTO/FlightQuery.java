package com.travel.flight.Flights.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlightQuery {
    private String startPoint;
    private String destination;
    private String leaveDate;
    private String returnDate;
}
