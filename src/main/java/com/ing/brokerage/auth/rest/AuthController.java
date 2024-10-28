package com.ing.brokerage.auth.rest;

import com.ing.brokerage.auth.model.*;
import com.ing.brokerage.auth.repo.AppUserRepository;
import com.ing.brokerage.auth.repo.RoleRepository;
import com.ing.brokerage.auth.util.JwtGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;

    @Autowired
    public AuthController(AppUserRepository appUserRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtGenerator jwtGenerator) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.jwtGenerator=jwtGenerator;
    }


    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {

        if (appUserRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(registerDto.getUsername());
        appUser.setPassword(passwordEncoder.encode((registerDto.getPassword())));
        Role roles = roleRepository.findByName(registerDto.getRole()).get();
        appUser.setRoles(Collections.singletonList(roles));

         appUserRepository.save(appUser);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerInfo>> getAllUsers() {
        List<CustomerInfo> clientInfos = appUserRepository.findAll().stream()
                .map(item -> CustomerInfo.builder()
                        .name(item.getUsername())
                        .customerId(item.getId())
                        .roles(item.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientInfos);
    }

}