package com.ing.brokerage.domain.order.infrastructure.db.entity;


import com.ing.brokerage.domain.order.domain.info.OrderSide;
import com.ing.brokerage.domain.order.domain.info.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide orderSide; // BUY or SELL

    @Column(nullable = false)
    private BigDecimal size; // Quantity of assets

    @Column(nullable = false)
    private BigDecimal price; // Price per asset

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; // PENDING, MATCHED, CANCELED

    @Column(nullable = false)
    private LocalDateTime createDate = LocalDateTime.now();

    @Version
    private Long version;
}
