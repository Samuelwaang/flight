package com.travel.flight.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.travel.flight.Flights.Flight;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Modifying
    @Query(value = "DELETE FROM user_flights", nativeQuery = true)
    void clearFlightAssociations();
  // List<Flight> findFlightsByUser(User user);  
}
