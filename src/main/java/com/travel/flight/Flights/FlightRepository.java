package com.travel.flight.Flights;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.travel.flight.Flights.DTO.Flight;

import jakarta.transaction.Transactional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Flight f WHERE f.leaveDate = :leaveDate AND f.returnDay = :returnDay")
    void deleteByLeaveDateAndReturnDay(String leaveDate, String returnDay);

    List<Flight> findByLeaveDateAndReturnDay(String leaveDate, String returnDay);

    List<Flight> findByLeaveDate(String leaveDate);

    @Query("SELECT f FROM Flight f WHERE f.leaveDate = (SELECT MAX(f2.leaveDate) FROM Flight f2)")
    List<Flight> findAllWithLatestLeaveDate();

    List<Flight> findByFlightStartAndFlightDestinationAndLeaveDateAndReturnDay(
            String flightStart, String flightDestination, String leaveDate, String returnDay);

    @Query("SELECT f FROM Flight f WHERE f.flightStart = :flightStart AND f.flightDestination = :flightDestination ORDER BY f.price ASC")
    List<Flight> findCheapestFlights(@Param("flightStart") String flightStart,
            @Param("flightDestination") String flightDestination, Pageable pageable);

    @Query("SELECT f FROM Flight f WHERE f.flightStart = :flightStart AND f.flightDestination = :flightDestination AND f.airline NOT IN :excludedAirlines ORDER BY f.price ASC")
    List<Flight> findCheapestFlightsExcludingAirlines(@Param("flightStart") String flightStart,
            @Param("flightDestination") String flightDestination,
            @Param("excludedAirlines") List<String> excludedAirlines,
            Pageable pageable);
}
