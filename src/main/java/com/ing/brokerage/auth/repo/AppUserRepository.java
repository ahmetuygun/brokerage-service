package com.ing.brokerage.auth.repo;

import com.ing.brokerage.auth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByUsername(String username);
    Boolean existsByUsername(String username);
}