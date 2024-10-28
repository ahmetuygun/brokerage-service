package com.ing.brokerage.domain.order.domain.info;

import com.ing.brokerage.common.ID;

public class OrderId implements ID {

    private Long id;
    public OrderId(Long id) {
        this.id = id;
    }

    @Override
    public Long value() {
        return id;
    }
}
