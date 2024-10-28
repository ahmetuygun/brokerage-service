package com.ing.brokerage.domain.order.application.event;

import com.ing.brokerage.domain.order.domain.info.OrderSide;
import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;


public class OrderCreatedEvent extends ApplicationEvent {
    private final Long customerId;
    private final String assetName;
    private final OrderSide orderSide;
    private final BigDecimal size;
    private final BigDecimal price;


    public OrderCreatedEvent(Object source, Long customerId, String assetName, OrderSide orderSide, BigDecimal size, BigDecimal price) {
        super(source);
        this.customerId = customerId;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getAssetName() {
        return assetName;
    }

    public OrderSide getOrderSide() {
        return orderSide;
    }

    public BigDecimal getSize() {
        return size;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
