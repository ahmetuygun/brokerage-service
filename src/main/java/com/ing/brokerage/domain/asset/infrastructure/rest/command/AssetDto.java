package com.ing.brokerage.domain.asset.infrastructure.rest.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDto {
    private Long id;
    private String assetName;
    private BigDecimal size;
    private BigDecimal usableSize;


}
