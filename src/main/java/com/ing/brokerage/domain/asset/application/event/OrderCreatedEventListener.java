package com.ing.brokerage.domain.asset.application.event;

import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.domain.asset.application.AssetService;
import com.ing.brokerage.domain.asset.domain.exception.AssetNotFoundException;
import com.ing.brokerage.domain.asset.domain.exception.InsufficientBalanceException;
import com.ing.brokerage.domain.order.application.event.OrderCanceledEvent;
import com.ing.brokerage.domain.order.application.event.OrderCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventListener {
    private final AssetService assetService;

    public OrderCreatedEventListener(AssetService assetService) {
        this.assetService = assetService;
    }


    @EventListener
    public void handleOrderCreation(OrderCreatedEvent orderCreatedEvent) throws DomainException {
        assetService.validateAndReserveForOrder(orderCreatedEvent);
    }

    @EventListener
    public void handleOrderCancellation(OrderCanceledEvent orderCanceledEvent) throws AssetNotFoundException {
       assetService.handleOrderCancellation(orderCanceledEvent);
    }

}
