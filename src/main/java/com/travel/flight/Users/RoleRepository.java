package com.travel.flight.Users;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long>{
    Optional<Role> findByEmail(String email);
}
