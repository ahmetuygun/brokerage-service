package com.ing.brokerage.domain.asset.domain.exception;

import com.ing.brokerage.common.DomainException;

public class AssetNotFoundException extends DomainException {
    public AssetNotFoundException(String message) {
        super(message);
    }
}
