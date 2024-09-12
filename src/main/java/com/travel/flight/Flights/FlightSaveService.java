package com.travel.flight.Flights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travel.flight.Flights.DTO.Flight;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightSaveService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> saveFlights(List<Flight> flights) {
        List<Flight> flightList = new ArrayList<>();
        Iterable<Flight> iterableFlights = flightRepository.saveAll(flights);
        for (Flight flight : iterableFlights) {
            flightList.add(flight);
        }
        return flightList;
    }
}