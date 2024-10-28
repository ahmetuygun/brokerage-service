package com.ing.brokerage.domain.asset.domain.exception;

import com.ing.brokerage.common.DomainException;

public class InsufficientBalanceException extends DomainException {
    public InsufficientBalanceException(String s) {
        super(s);
    }
}
