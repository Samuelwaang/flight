package com.travel.flight.Flights.DTO;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Stop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private int time;
    
    @ManyToOne
    @JoinColumn(name = "flight_id")
    @JsonBackReference(value = "flightStops")
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "return_flight_id")
    @JsonBackReference(value = "returnFlightStops")
    private Flight returnFlight;
}