package com.ing.brokerage.domain.asset.domain;

import com.ing.brokerage.domain.asset.domain.model.Asset;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.AssetDto;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {

    Optional<Asset> retrieveAsset(Long customerId, String assetName);

    void save(Asset asset);

    Long generateId();

    List<AssetDto> listAssetByCustomer(Long customerId);
}
