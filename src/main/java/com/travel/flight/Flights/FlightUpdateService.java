package com.travel.flight.Flights;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.flight.Flights.DTO.Flight;
import com.travel.flight.Flights.DTO.FlightPageEntity;
import com.travel.flight.Flights.DTO.FlightQuery;
import com.travel.flight.Flights.DTO.LeaveDatePointer;
import com.travel.flight.Flights.DTO.Stop;
import com.travel.flight.Flights.DTO.UpdateFlightQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightUpdateService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private LeaveDatePointerRepository pointerRepository;

    @Autowired
    private FlightDataReceiverService flightDataReceiverService;
    @Autowired
    private FlightSaveService flightSaveService;

    private static final String POINTER_ID = "lastLeaveDatePointer";

    private final WebClient.Builder webClientBuilder;

    public FlightUpdateService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

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
                    } else {
                        return currentPageFlights;
                    }
                });
    }

    public void newPrices() {
        WebClient webClient = webClientBuilder.build();

        Flux<Flight> flightFlux = getAllFlights(0, 50);

        flightFlux.groupBy(flight -> List.of(
                flight.getLeaveDate(),
                flight.getReturnDay(),
                flight.getFlightStart(),
                flight.getFlightDestination()))
                .concatMap(groupedFlux -> groupedFlux.collectList()
                        .flatMap(list -> webClient.post()
                                .uri("http://localhost:8082/data/update-price-link")
                                .bodyValue(list)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<List<UpdateFlightQuery>>() {
                                })))
                .concatMap(updateFlightQueries -> Flux.fromIterable(updateFlightQueries))
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

    public void scheduleTask() {
        LeaveDatePointer pointer = pointerRepository.findById(POINTER_ID)
                .orElse(new LeaveDatePointer(POINTER_ID, null));

        List<Flight> latestFlights = flightRepository.findAllWithLatestLeaveDate();

        if (!latestFlights.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate leaveDate = LocalDate.parse(latestFlights.get(0).getLeaveDate(), formatter);
            LocalDate nextFriday = leaveDate.with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.FRIDAY));
            LocalDate nextSaturday = nextFriday.plusDays(1);
            LocalDate nextSunday = nextSaturday.plusDays(1);
            LocalDate nextMonday = nextSunday.plusDays(1);

            makeFlightQueries(nextFriday, nextSaturday, nextSunday, nextMonday, "san", "pit");

            pointer.setLastProcessedLeaveDate(latestFlights.get(0).getLeaveDate());
            pointerRepository.save(pointer);

            System.out.println("Next Friday after " + leaveDate + " is " + nextFriday);
        }
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

    public ResponseEntity<String> makeFlightQueries(LocalDate friday, LocalDate saturday, LocalDate sunday,
            LocalDate monday, String startPoint, String destination) {
        ObjectMapper objectMapper = new ObjectMapper();

        FlightQuery fridayToSunday = new FlightQuery(startPoint, destination, friday.toString(), sunday.toString(),
                "fridayToSunday");
        FlightQuery fridayToMonday = new FlightQuery(startPoint, destination, friday.toString(), monday.toString(),
                "fridayToMonday");
        FlightQuery saturdayToSunday = new FlightQuery(startPoint, destination, saturday.toString(), sunday.toString(),
                "saturdayToSunday");
        FlightQuery saturdayToMonday = new FlightQuery(startPoint, destination, saturday.toString(), monday.toString(),
                "saturdayToMonday");

        try {
            saveFlight(objectMapper.writeValueAsString(fridayToSunday));
            saveFlight(objectMapper.writeValueAsString(fridayToMonday));
            saveFlight(objectMapper.writeValueAsString(saturdayToSunday));
            saveFlight(objectMapper.writeValueAsString(saturdayToMonday));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving flight data: " + e.getMessage());
        }
        return ResponseEntity.ok("Flight data initialized successfully");
    }

    @Autowired
    private JavaMailSender mailSender;

    public void sendNotification(String userEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Price Alert: Flight Price Dropped");
        message.setText("hi");

        mailSender.send(message);
    }

}
