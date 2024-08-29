package com.travel.flight.Flights;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.travel.flight.Flights.DTO.Flight;
import com.travel.flight.Flights.DTO.FlightPageEntity;
import com.travel.flight.Flights.DTO.UpdateFlightQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import reactor.core.publisher.Flux;
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

    // public List<Flight> getAllFlights() {
    //     String url = "http://localhost:8081/flight/all";

    //     Mono<List<Flight>> flightsMono = webClientBuilder.build()
    //             .get()
    //             .uri(url)
    //             .retrieve()
    //             .bodyToMono(new ParameterizedTypeReference<List<Flight>>() {});

    //     return flightsMono.block();
    // }

    public Flux<Flight> getAllFlights(int page, int size) {
        String url = String.format("http://localhost:8081/flight/paged?page=%d&size=%d", page, size);
    
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(FlightPageEntity.class)
                .flatMapMany(flightPage -> {
                    Flux<Flight> currentPageFlights = Flux.fromIterable(flightPage.getContent());
                    
                    if (flightPage.getNumber() < flightPage.getTotalPages() - 1) {
                        return Flux.concat(currentPageFlights, getAllFlights(page + 1, size));
                    } 
                    else {
                        return currentPageFlights;
                    }
                });
    }

    public void newPrices() {
        WebClient webClient = webClientBuilder.build();
    
        Flux<Flight> flightFlux = getAllFlights(0, 50);
    
        flightFlux.groupBy(flight -> 
            List.of(
                flight.getLeaveDate(), 
                flight.getReturnDay(), 
                flight.getFlightStart(), 
                flight.getFlightDestination()
            )
        )
        .concatMap(groupedFlux -> 
            groupedFlux.collectList() 
                .flatMap(list -> 
                    webClient.post()
                        .uri("http://localhost:8082/data/update-price-link")
                        .bodyValue(list) 
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<UpdateFlightQuery>>() {}) 
                )
        )
        .concatMap(updateFlightQueries -> 
            Flux.fromIterable(updateFlightQueries) 
        )
        .doOnNext(updateFlightQuery -> {
            Optional<Flight> optionalFlight = flightRepository.findById(updateFlightQuery.getId());
            Flight existingFlight = optionalFlight.get();

            System.out.println("Updating flight: " + updateFlightQuery.getId());

            existingFlight.setPrice(updateFlightQuery.getPrice());
            flightRepository.save(existingFlight);
        })
        .subscribe(); 
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
    // public void callNewPricesApi() {
    //     String url = "http://localhost:8082/data/update-price-link";
    //     List<Flight> flights = getAllFlights();
    //     List<List<Flight>> groupedFlights = groupFlights(flights);
    //     for(List<Flight> subFlightList : groupedFlights) {
    //         Mono<List<UpdateFlightQuery>> updateFlightQueriesMono = webClientBuilder.build()
    //         .post()
    //         .uri(url)
    //         .bodyValue(subFlightList)
    //         .retrieve()
    //         .bodyToMono(new ParameterizedTypeReference<List<UpdateFlightQuery>>() {});

    //         for(UpdateFlightQuery updateFlightQuery : updateFlightQueriesMono.block()) {
    //             // update flight with new price and link
    //             Optional<Flight> optionalFlight = flightRepository.findById(updateFlightQuery.getId());
    //             Flight existingFlight = optionalFlight.get();

    //             System.out.println("Updating flight: " + updateFlightQuery.getId());

    //             existingFlight.setPrice(updateFlightQuery.getPrice());
    //             existingFlight.setLink(updateFlightQuery.getLink());
    //             flightRepository.save(existingFlight);
    //         }
    //     }
    // }

    // public void testGroupingFlights() {
    //     // Step 1: Retrieve the data
    //     List<Flight> flights = getAllFlights();

    //     // Step 2: Group the data
    //     List<List<Flight>> groupedFlights = groupFlights(flights);

    //     // Step 3: Print the grouped data
    //     for (List<Flight> group : groupedFlights) {
    //         System.out.println("Group:");
    //         for (Flight flight : group) {
    //             System.out.println(flight);
    //         }
    //         System.out.println(); // Add a blank line between groups
    //     }
    // }

    // // updates just the prices
    // public void callJustNewPricesApi() {
    //     String url = "http://localhost:8082/data/update-price";
    //     List<Flight> flights = getAllFlights();
    //     List<List<Flight>> groupedFlights = groupFlights(flights);
    //     for(List<Flight> subFlightList : groupedFlights) {
    //         Mono<List<UpdateFlightQuery>> updateFlightQueriesMono = webClientBuilder.build()
    //         .post()
    //         .uri(url)
    //         .bodyValue(subFlightList)
    //         .retrieve()
    //         .bodyToMono(new ParameterizedTypeReference<List<UpdateFlightQuery>>() {});

    //         for(UpdateFlightQuery updateFlightQuery : updateFlightQueriesMono.block()) {
    //             // update flight with new price and link
                // Optional<Flight> optionalFlight = flightRepository.findById(updateFlightQuery.getId());
                // Flight existingFlight = optionalFlight.get();

                // System.out.println("Updating flight: " + updateFlightQuery.getId());

                // existingFlight.setPrice(updateFlightQuery.getPrice());
                // flightRepository.save(existingFlight);
    //         }
    //     }
    // }
}
