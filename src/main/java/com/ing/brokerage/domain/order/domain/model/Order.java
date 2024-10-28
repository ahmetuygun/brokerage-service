package com.ing.brokerage.domain.order.domain.model;

import com.ing.brokerage.auth.model.CustomUserDetails;
import com.ing.brokerage.auth.model.RoleEnum;
import com.ing.brokerage.common.AggregateRoot;
import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.common.SecurityUtil;
import com.ing.brokerage.domain.asset.domain.exception.UnauthorizedAccessException;
import com.ing.brokerage.domain.order.application.event.OrderMatchedEvent;
import com.ing.brokerage.domain.order.domain.exception.InvalidOrderStateException;
import com.ing.brokerage.domain.order.domain.info.OrderId;
import com.ing.brokerage.domain.order.domain.info.OrderSide;
import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import com.ing.brokerage.domain.order.application.event.OrderCanceledEvent;
import com.ing.brokerage.domain.order.application.event.OrderCreatedEvent;
import com.ing.brokerage.domain.order.infrastructure.rest.command.OrderRequest;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order extends AggregateRoot<OrderId> {
    protected Order(OrderId id) {
        super(id);
    }

    private Long customerId;

    private String assetName;

    private OrderSide orderSide;

    private BigDecimal size;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDateTime createDate;

    public Order(OrderId id, Long customer, String assetName, OrderSide orderSide, BigDecimal size, BigDecimal price) {
        super(id);
        this.customerId = customer;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
        this.status = OrderStatus.PENDING;
        this.createDate = LocalDateTime.now();
        updateVersion(0L);

    }

    public Order(OrderId id, Long customer, String assetName, OrderSide orderSide, BigDecimal size, BigDecimal price, OrderStatus status, LocalDateTime createDate, Long version) {
        super(id);
        this.customerId = customer;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
        this.status = status;
        this.createDate = createDate;
        updateVersion(version);

    }

    public Long customerId() {
        return customerId;
    }

    public String assetName() {
        return assetName;
    }

    public OrderSide orderSide() {
        return orderSide;
    }

    public BigDecimal size() {
        return size;
    }

    public BigDecimal price() {
        return price;
    }

    public OrderStatus status() {
        return status;
    }

    public LocalDateTime createDate() {
        return createDate;
    }


    public void validateForCancellation() throws DomainException {

        validatePermission(customerId);

        if (status() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only pending orders can be canceled");
        }

        OrderCanceledEvent orderCreatedEvent = new OrderCanceledEvent(this,
                this,
                this.customerId,
                this.assetName,
                this.size
        );
        this.registerEvent(orderCreatedEvent);


    }

    public void validateForNew(OrderRequest orderRequest) throws UnauthorizedAccessException {

        validatePermission(orderRequest.getCustomerId());

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(this,
                this.customerId,
                this.assetName,
                this.orderSide,
                this.size,
                this.price
        );

        this.registerEvent(orderCreatedEvent);
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    public void match() {
        this.status = OrderStatus.MATCHED;
        OrderMatchedEvent orderCreatedEvent = new OrderMatchedEvent(this,this);
        this.registerEvent(orderCreatedEvent);
    }

    public void validatePermission(Long customerId) throws UnauthorizedAccessException {
        CustomUserDetails userDetail = SecurityUtil.getAuthenticatedUser();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDetail.getAuthorities();

        boolean isUserRole = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(RoleEnum.ROLE_USER.name()));

        if (isUserRole && !customerId.equals(userDetail.getUserId())) {
            throw new UnauthorizedAccessException("Customer ID does not match the authenticated user's ID.");
        }

    }


}
