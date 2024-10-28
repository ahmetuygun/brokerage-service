package com.ing.brokerage.domain.order.infrastructure.rest;

import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.domain.order.application.OrderService;
import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import com.ing.brokerage.domain.order.infrastructure.rest.command.OrderInfo;
import com.ing.brokerage.domain.order.infrastructure.rest.command.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class MatchController {

    private final OrderService orderService;

    public MatchController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/match-orders")
    public ResponseEntity<String> matchPendingOrders() throws DomainException {
        orderService.matchPendingOrders();
        return ResponseEntity.ok("Pending orders matched successfully.");
    }
}
