package com.travel.flight.Flights;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnedFlight {
    private long id;
    private String airline;
    private int time;
    private double price;
    private String link;
    private String flightStart;
    private String flightDestination;
    private String leaveTime;
    private String arrivalTime;
    private String leaveDate;
    private String returnDay;
    private List<Stop> stops;
    private int numStops;
}
