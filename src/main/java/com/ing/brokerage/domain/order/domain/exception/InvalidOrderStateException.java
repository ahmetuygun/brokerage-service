package com.ing.brokerage.domain.order.domain.exception;

import com.ing.brokerage.common.DomainException;

public class InvalidOrderStateException extends DomainException {
    public InvalidOrderStateException(String s) {
        super(s);
    }
}
