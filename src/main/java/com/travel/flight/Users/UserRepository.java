package com.travel.flight.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Modifying
    @Query(value = "DELETE FROM user_flights", nativeQuery = true)
    void clearFlightAssociations();

    List<UserEntity> findAll();
}
