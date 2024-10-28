package com.ing.brokerage.domain.order.domain.repository;

import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import com.ing.brokerage.domain.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Long generateId();
    void save(Order order);
    Optional<Order> retrieveOrder(Long orderId);
    Page<Order> listOrder(Long customerId, OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Order> fetchPendingOrders();
}
