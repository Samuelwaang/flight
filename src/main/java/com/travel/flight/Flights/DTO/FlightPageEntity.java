package com.travel.flight.Flights.DTO;

import java.util.List;

import lombok.Data;

@Data
public class FlightPageEntity {
    private List<Flight> content;
    private int number;
    private int totalPages; 
    private boolean last;
}