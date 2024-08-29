package com.travel.flight.Flights;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travel.flight.Flights.DTO.LeaveDatePointer;

public interface LeaveDatePointerRepository extends JpaRepository<LeaveDatePointer, String> {
}