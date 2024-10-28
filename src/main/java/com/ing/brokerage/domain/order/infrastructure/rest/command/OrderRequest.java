package com.ing.brokerage.domain.order.infrastructure.rest.command;

import com.ing.brokerage.domain.order.domain.info.OrderSide;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotNull(message = "Customer ID is required.")
    private Long customerId;

    @NotNull(message = "Asset name is required.")
    private String assetName;

    @NotNull(message = "Order side is required.")
    private OrderSide orderSide;

    @Min(value = 1, message = "Size must be at least 1.")
    private BigDecimal size;

    @NotNull(message = "Price is required.")
    @Min(value = 0, message = "Price must be non-negative.")
    private BigDecimal price;


}
