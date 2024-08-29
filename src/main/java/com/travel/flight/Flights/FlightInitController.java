package com.travel.flight.Flights;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.flight.Flights.DTO.Flight;
import com.travel.flight.Flights.DTO.FlightQuery;
import com.travel.flight.Flights.DTO.Stop;
import com.travel.flight.Users.UserRepository;

@RestController
@RequestMapping("/init")
public class FlightInitController {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private FlightDataReceiverService flightDataReceiverService;
    @Autowired
    private FlightSaveService flightSaveService;

    private List<LocalDate> fridays = new ArrayList<>();
    private List<LocalDate> saturdays = new ArrayList<>();
    private List<LocalDate> sundays = new ArrayList<>();
    private List<LocalDate> mondays = new ArrayList<>();

    @PostMapping("/save")
    public ResponseEntity<String> init(@RequestParam String startPoint, @RequestParam String destination) {
        getDates(); // Ensure dates are populated before using them
        ObjectMapper objectMapper = new ObjectMapper();
        for (int i = 0; i < fridays.size(); i++) {
            FlightQuery fridayToSunday = new FlightQuery(startPoint, destination, fridays.get(i).toString(), sundays.get(i).toString());
            FlightQuery fridayToMonday = new FlightQuery(startPoint, destination, fridays.get(i).toString(), mondays.get(i).toString());
            FlightQuery saturdayToSunday = new FlightQuery(startPoint, destination, saturdays.get(i).toString(), sundays.get(i).toString());
            FlightQuery saturdayToMonday = new FlightQuery(startPoint, destination, saturdays.get(i).toString(), mondays.get(i).toString());

            try {
                saveFlight(objectMapper.writeValueAsString(fridayToSunday));
                saveFlight(objectMapper.writeValueAsString(fridayToMonday));
                saveFlight(objectMapper.writeValueAsString(saturdayToSunday));
                saveFlight(objectMapper.writeValueAsString(saturdayToMonday));
            } 
            catch (Exception e) {
                return ResponseEntity.status(500).body("Error saving flight data: " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Flight data initialized successfully");
    }

    private void saveFlight(String jsonBody) throws IOException {
        String apiUrl = "http://localhost:8082/data/get";
        List<Flight> flights = flightDataReceiverService.callExternalApi(apiUrl, jsonBody);
        flightSaveService.saveFlights(flights);

        for (Flight flight : flights) {
            for (Stop stop : flight.getStops()) {
                stop.setFlight(flight);
            }
        }
    }

    private void getDates() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);

        LocalDate nextFriday = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        LocalDate nextSaturday = nextFriday.plusDays(1);
        LocalDate nextSunday = nextSaturday.plusDays(1);
        LocalDate nextMonday = nextSunday.plusDays(1);

        while ((nextFriday.isBefore(endDate) || nextFriday.isEqual(endDate)) &&
               (nextSaturday.isBefore(endDate) || nextSaturday.isEqual(endDate)) &&
               (nextSunday.isBefore(endDate) || nextSunday.isEqual(endDate)) &&
               (nextMonday.isBefore(endDate) || nextMonday.isEqual(endDate))) {

            fridays.add(nextFriday);
            saturdays.add(nextSaturday);
            sundays.add(nextSunday);
            mondays.add(nextMonday);

            nextFriday = nextFriday.plusWeeks(1);
            nextSaturday = nextSaturday.plusWeeks(1);
            nextSunday = nextSunday.plusWeeks(1);
            nextMonday = nextMonday.plusWeeks(1);
        }

        int minSize = Math.min(Math.min(fridays.size(), saturdays.size()), Math.min(sundays.size(), mondays.size()));
        while (fridays.size() > minSize) fridays.remove(fridays.size() - 1);
        while (saturdays.size() > minSize) saturdays.remove(saturdays.size() - 1);
        while (sundays.size() > minSize) sundays.remove(sundays.size() - 1);
        while (mondays.size() > minSize) mondays.remove(mondays.size() - 1);
    }

    @GetMapping("get-all")
    public @ResponseBody String getAll() {
        List<Flight> flights = flightDataReceiverService.getAllFlights();
        return flights.toString();
    }
}
