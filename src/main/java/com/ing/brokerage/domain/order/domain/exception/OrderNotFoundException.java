package com.ing.brokerage.domain.order.domain.exception;

import com.ing.brokerage.common.DomainException;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(String s) {
        super(s);
    }
}
