package com.travel.flight.Flights;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightUpdateService {

    @Autowired
    private FlightRepository flightRepository;

    private final WebClient.Builder webClientBuilder;

    public FlightUpdateService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public List<Flight> getAllFlights() {
        String url = "http://localhost:8081/flight/all";

        Mono<List<Flight>> flightsMono = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Flight>>() {});

        return flightsMono.block();
    }

    public List<List<Flight>> groupFlights(List<Flight> flights) {
        return flights.stream()
                .collect(Collectors.groupingBy(flight -> flight.getFlightStart() + flight.getFlightDestination() +
                        flight.getLeaveDate() + flight.getReturnDay()))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    //updates the price and link to the flight
    public void callNewPricesApi() {
        String url = "http://localhost:8082/data/update-price-link";
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

                System.out.println("Updating flight: " + updateFlightQuery.getId());

                existingFlight.setPrice(updateFlightQuery.getPrice());
                existingFlight.setLink(updateFlightQuery.getLink());
                flightRepository.save(existingFlight);
            }
        }
    }

    public void testGroupingFlights() {
        // Step 1: Retrieve the data
        List<Flight> flights = getAllFlights();

        // Step 2: Group the data
        List<List<Flight>> groupedFlights = groupFlights(flights);

        // Step 3: Print the grouped data
        for (List<Flight> group : groupedFlights) {
            System.out.println("Group:");
            for (Flight flight : group) {
                System.out.println(flight);
            }
            System.out.println(); // Add a blank line between groups
        }
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

                System.out.println("Updating flight: " + updateFlightQuery.getId());

                existingFlight.setPrice(updateFlightQuery.getPrice());
                flightRepository.save(existingFlight);
            }
        }
    }
}
