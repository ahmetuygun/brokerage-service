package com.ing.brokerage.domain.order.infrastructure.rest.command;

import com.ing.brokerage.domain.order.domain.info.OrderSide;
import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import com.ing.brokerage.domain.order.domain.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderInfo {

    private Long orderId;
    private Long customerId;
    private String assetName;
    private OrderSide orderSide;
    private BigDecimal size;
    private BigDecimal price;
    private OrderStatus status;
    private LocalDateTime createDate;

    public OrderInfo(Order order) {
        this.orderId = order.getId().value();
        this.customerId = order.customerId();
        this.assetName = order.assetName();
        this.orderSide = order.orderSide();
        this.size = order.size();
        this.price = order.price();
        this.status = order.status();
        this.createDate = order.createDate();
    }

}
