package com.ing.brokerage.domain.asset.domain.exception;

import com.ing.brokerage.common.DomainException;

public class UnauthorizedAccessException  extends DomainException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}