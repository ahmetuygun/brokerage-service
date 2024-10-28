package com.ing.brokerage.domain.asset.domain.model;

import com.ing.brokerage.common.ID;

public class AssetId implements ID {
    private Long id;

    public AssetId(Long id) {
        this.id = id;

    }

    @Override
    public Long value() {
        return id;
    }
}
