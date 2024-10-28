package com.ing.brokerage.auth.model;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String role;
}
