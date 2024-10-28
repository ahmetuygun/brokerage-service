package com.ing.brokerage.domain.asset.infrastructure.rest.command;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class DepositRequest {
    private Long customerId;
    private BigDecimal size;

    public DepositRequest(Long customerId, BigDecimal size) {
        this.customerId = customerId;
        this.size = size;
    }
}
