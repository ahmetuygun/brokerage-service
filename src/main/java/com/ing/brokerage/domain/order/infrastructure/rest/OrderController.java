package com.ing.brokerage.domain.order.infrastructure.rest;

import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.domain.order.application.OrderService;
import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import com.ing.brokerage.domain.order.domain.exception.InvalidOrderStateException;
import com.ing.brokerage.domain.order.infrastructure.rest.command.OrderInfo;
import com.ing.brokerage.domain.order.infrastructure.rest.command.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController( OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@RequestBody OrderRequest orderRequest) throws DomainException {
        orderService.processOrder(orderRequest);
    }


    @PostMapping("cancel/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public void cancelOrder(@PathVariable Long orderId) throws DomainException {
        orderService.cancelOrder(orderId);
    }

    @GetMapping
    public ResponseEntity<Page<OrderInfo>> listOrders(
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "status", required = false) OrderStatus status,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createDate,desc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate")));
        Page<OrderInfo> orders = orderService.listOrder(customerId, status, startDate, endDate, pageable);
        return ResponseEntity.ok(orders);
    }

}
