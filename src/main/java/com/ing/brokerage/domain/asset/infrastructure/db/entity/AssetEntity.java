package com.ing.brokerage.domain.asset.infrastructure.db.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;


@Entity
@Data
public class AssetEntity {
    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String assetName;

    @Column(nullable = false)
    private BigDecimal size;

    @Column(nullable = false)
    private BigDecimal usableSize;

    @Version
    private Long version;
}
