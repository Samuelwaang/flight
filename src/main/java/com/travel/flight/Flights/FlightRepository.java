package com.travel.flight.Flights;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.travel.flight.Flights.DTO.Flight;

import jakarta.transaction.Transactional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Flight f WHERE f.leaveDate = :leaveDate AND f.returnDay = :returnDay")
    void deleteByLeaveDateAndReturnDay(String leaveDate, String returnDay);

    List<Flight> findByLeaveDateAndReturnDay(String leaveDate, String returnDay);

    List<Flight> findByLeaveDate(String leaveDate);
}
