package com.travel.flight.Flights;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.travel.flight.Flights.DTO.Flight;
import com.travel.flight.Flights.DTO.UpdateFlightQuery;
import com.travel.flight.Users.UserEntity;
import com.travel.flight.Users.UserRepository;

import reactor.core.publisher.Mono;

@Component
@EnableScheduling
public class SchedulingTasks {
    @Autowired
	private FlightRepository flightRepository;
    @Autowired
	private UserRepository userRepository;

    private final WebClient.Builder webClientBuilder;

    public SchedulingTasks(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Scheduled(cron = "59 59 23 * * *", zone = "America/Los_Angeles")
    public void deleteOldFlights() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Flight> flights = flightRepository.findByLeaveDate(yesterday.toString());

        if (flights.isEmpty()) {
            System.out.println("Empty");
        }

        // clear associations for each flight
        List<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            user.getFlights().removeAll(flights); 
        }
        userRepository.saveAll(users);

        // delete flights
        flightRepository.deleteAll(flights);
    }

    @Scheduled(cron = "0 0 0/3 * * ?")
    public void updatePrice() {
        callJustNewPricesApi();
    }

    public List<List<Flight>> groupFlights(List<Flight> flightsIterable) {
        return StreamSupport.stream(flightsIterable.spliterator(), false)
                .collect(Collectors.groupingBy(flight -> flight.getFlightStart() + flight.getFlightDestination() +
                        flight.getLeaveDate() + flight.getReturnDay()))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    // updates just the prices
    public void callJustNewPricesApi() {
        String url = "http://localhost:8082/data/update-price";
        List<Flight> flights = getAllFlights();
        List<List<Flight>> groupedFlights = groupFlights(flights);
        for(List<Flight> subFlightList : groupedFlights) {
            Mono<List<UpdateFlightQuery>> updateFlightQueriesMono = webClientBuilder.build()
            .post()
            .uri(url)
            .bodyValue(subFlightList)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<UpdateFlightQuery>>() {});

            for(UpdateFlightQuery updateFlightQuery : updateFlightQueriesMono.block()) {
                // update flight with new price and link
                Optional<Flight> optionalFlight = flightRepository.findById(updateFlightQuery.getId());
                Flight existingFlight = optionalFlight.get();

                existingFlight.setPrice(updateFlightQuery.getPrice());
                flightRepository.save(existingFlight);
            }
        }
    }

    public List<Flight> getAllFlights() {
        String url = "http://localhost:8081/flight/all";

        try {
            Mono<List<Flight>> flightsMono = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Flight>>() {});
            return flightsMono.block();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve flights", e);
        }
    }
}
