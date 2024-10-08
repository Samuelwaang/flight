package com.travel.flight.Flights;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.flight.Flights.DTO.Flight;

import reactor.core.publisher.Mono;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FlightDataReceiverService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public FlightDataReceiverService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Flight> callExternalApi(String url, String jsonBody) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class);

        String responseBody = response.getBody();
        return objectMapper.readValue(responseBody, new TypeReference<List<Flight>>() {
        });
    }

    public List<Flight> getAllFlights() {
        String url = "http://localhost:8081/flight/all";

        Mono<List<Flight>> flightsMono = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Flight>>() {
                });

        return flightsMono.block();
    }
}
