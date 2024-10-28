package com.ing.brokerage.domain.asset.infrastructure;

import com.ing.brokerage.domain.asset.domain.model.Asset;
import com.ing.brokerage.domain.asset.domain.AssetRepository;
import com.ing.brokerage.domain.asset.domain.model.AssetId;
import com.ing.brokerage.domain.asset.infrastructure.db.entity.AssetEntity;
import com.ing.brokerage.domain.asset.infrastructure.db.repository.AssetEntityRepository;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.AssetDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetRepositoryImpl implements AssetRepository {


    private final AssetEntityRepository assetEntityRepository;

    public AssetRepositoryImpl(AssetEntityRepository assetEntityRepository) {
        this.assetEntityRepository = assetEntityRepository;
    }

    @Override
    public Optional<Asset> retrieveAsset(Long customerId, String assetName) {
        return assetEntityRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .map(entity -> new Asset(
                        new AssetId(entity.getId()),
                        entity.getCustomerId(),
                        entity.getAssetName(),
                        entity.getSize(),
                        entity.getUsableSize(),
                        entity.getVersion()
                ));
    }


    @Override
    public void save(Asset asset) {
        AssetEntity assetEntity =  toEntity(asset);
        assetEntityRepository.save(assetEntity);
    }

    @Override
    public Long generateId() {
        Long maxId = assetEntityRepository.findMaxId();
        return (maxId != null) ? maxId + 1 : 1;
    }

    @Override
    public List<AssetDto> listAssetByCustomer(Long customerId) {

        List<AssetEntity> assets = assetEntityRepository.findByCustomerId(customerId);
        return assets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AssetDto convertToDto(AssetEntity asset) {
        return new AssetDto(
                asset.getId(),
                asset.getAssetName(),
                asset.getSize(),
                asset.getUsableSize()
        );
    }

    private AssetEntity toEntity(Asset asset) {
        AssetEntity assetEntity = new AssetEntity();
        assetEntity.setId(asset.getId().value());
        assetEntity.setCustomerId(asset.customerId());
        assetEntity.setAssetName(asset.assetName());
        assetEntity.setSize(asset.size());
        assetEntity.setUsableSize(asset.usableSize());
        assetEntity.setVersion(asset.version());
        return assetEntity;
    }
}


