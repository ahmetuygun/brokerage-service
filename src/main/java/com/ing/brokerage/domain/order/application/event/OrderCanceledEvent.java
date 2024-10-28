package com.ing.brokerage.domain.order.application.event;

import com.ing.brokerage.domain.order.domain.model.Order;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

public class OrderCanceledEvent extends ApplicationEvent {

    private final Order order;
    private final Long customerId;
    private final String assetName;
    private final BigDecimal size;

    public OrderCanceledEvent(Object source, Order order, Long customerId, String assetName, BigDecimal size) {
        super(source);
        this.order = order;
        this.customerId = customerId;
        this.assetName = assetName;
        this.size = size;
    }

    public Order getOrder() {
        return order;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getAssetName() {
        return assetName;
    }

    public BigDecimal getSize() {
        return size;
    }
}
