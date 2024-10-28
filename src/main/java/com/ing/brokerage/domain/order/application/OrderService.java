package com.ing.brokerage.domain.order.application;

import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.domain.order.domain.info.OrderId;
import com.ing.brokerage.domain.order.domain.exception.OrderNotFoundException;
import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import com.ing.brokerage.domain.order.domain.model.Order;
import com.ing.brokerage.domain.order.domain.repository.OrderRepository;
import com.ing.brokerage.domain.order.infrastructure.rest.command.OrderInfo;
import com.ing.brokerage.domain.order.infrastructure.rest.command.OrderRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.orderRepository = orderRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void processOrder(OrderRequest orderRequest) throws DomainException {
        Order order = new Order(new OrderId(orderRepository.generateId()), orderRequest.getCustomerId(), orderRequest.getAssetName(), orderRequest.getOrderSide(), orderRequest.getSize(),orderRequest.getPrice() );
        order.validateForNew(orderRequest);
        order.getDomainEvents().stream().forEach(applicationEventPublisher::publishEvent);
        order.clearDomainEvents();
        orderRepository.save(order);

    }

    public void cancelOrder(Long orderId) throws DomainException {
        Order order = orderRepository.retrieveOrder(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for ID: " + orderId));

        order.validateForCancellation();
        order.cancel();
        order.getDomainEvents().stream().forEach(applicationEventPublisher::publishEvent);
        order.clearDomainEvents();
        orderRepository.save(order);

    }

    public Page<OrderInfo> listOrder(Long customerId, OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Order> orders = orderRepository.listOrder(customerId, status, startDate, endDate, pageable);
        return orders.map(order -> new OrderInfo(order));
    }
}