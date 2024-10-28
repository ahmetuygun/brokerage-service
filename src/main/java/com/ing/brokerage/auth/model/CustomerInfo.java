package com.ing.brokerage.auth.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerInfo {

    private Long customerId;
    private String name;
    private List<String> roles;
}
