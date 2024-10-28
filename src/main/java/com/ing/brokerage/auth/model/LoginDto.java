package com.ing.brokerage.auth.model;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
}