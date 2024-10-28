package com.ing.brokerage.domain.order.application.event;

import com.ing.brokerage.domain.order.domain.model.Order;
import org.springframework.context.ApplicationEvent;

public class OrderMatchedEvent extends ApplicationEvent {

    private final Order order;


    public OrderMatchedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

}
