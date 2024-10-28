package com.ing.brokerage.auth.util;

import com.ing.brokerage.auth.model.Role;
import com.ing.brokerage.auth.repo.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(new Role(1,"ROLE_ADMIN"));
            }
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                roleRepository.save(new Role(2,"ROLE_USER"));
            }
        };
    }
}