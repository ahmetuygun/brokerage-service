package com.ing.brokerage.domain.asset.infrastructure.rest.command;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private Long customerId;
    private BigDecimal size;
    private String iban;

    public WithdrawRequest(Long customerId, BigDecimal size) {
        this.customerId = customerId;
        this.size = size;

    }
}
