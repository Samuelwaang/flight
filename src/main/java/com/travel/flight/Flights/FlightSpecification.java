package com.travel.flight.Flights;

import org.springframework.data.jpa.domain.Specification;

import com.travel.flight.Flights.DTO.Flight;

import jakarta.persistence.criteria.Predicate;

import java.util.List;

public class FlightSpecification {

    public static Specification<Flight> flightStartEquals(String flightStart) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("flightStart"), flightStart);
    }

    public static Specification<Flight> flightDestinationEquals(String flightDestination) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("flightDestination"),
                flightDestination);
    }

    public static Specification<Flight> excludeAirlines(List<String> excludedAirlines) {
        return (root, query, criteriaBuilder) -> {
            if (excludedAirlines == null || excludedAirlines.isEmpty()) {
                return criteriaBuilder.conjunction(); // No exclusion, return all
            }

            Predicate predicate = criteriaBuilder.conjunction();
            for (String airline : excludedAirlines) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.notLike(root.get("airline"), "%" + airline + "%"));
            }

            return predicate;
        };
    }
}