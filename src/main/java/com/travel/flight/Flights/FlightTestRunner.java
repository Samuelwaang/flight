package com.travel.flight.Flights;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FlightTestRunner implements CommandLineRunner {
 
    private final FlightUpdateService flightService;

    public FlightTestRunner(FlightUpdateService flightService) {
        this.flightService = flightService;
    }

    // @Override
    // public void run(String... args) {
    //     flightService.testGroupingFlights();
    // }

    // @Override
    // public void run(String... args) {
    //     flightService.callNewPricesApi();
    // }

    // @Override
    // public void run(String... args) {
    //     flightService.callJustNewPricesApi();
    // }

    @Override
    public void run(String... args) {
        flightService.scheduleTask();
    }
}