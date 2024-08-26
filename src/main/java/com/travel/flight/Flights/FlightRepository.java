package com.travel.flight.Flights;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

public interface FlightRepository extends CrudRepository<Flight, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Flight f WHERE f.leaveDate = :leaveDate AND f.returnDay = :returnDay")
    void deleteByLeaveDateAndReturnDay(String leaveDate, String returnDay);
}
