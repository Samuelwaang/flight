package com.travel.flight.Users;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.travel.flight.Flights.Flight;

public interface UserRepository extends CrudRepository<User, Long> {
  User findByEmail(String email);
  // List<Flight> findFlightsByUser(User user);  
}
