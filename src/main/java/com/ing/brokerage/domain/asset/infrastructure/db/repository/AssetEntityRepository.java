package com.ing.brokerage.domain.asset.infrastructure.db.repository;

import com.ing.brokerage.domain.asset.infrastructure.db.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetEntityRepository extends JpaRepository<AssetEntity, Long> {
    Optional<AssetEntity> findByCustomerIdAndAssetName(Long customerId, String assetName);

    @Query("SELECT MAX(a.id) FROM AssetEntity a")
    Long findMaxId();

    List<AssetEntity> findByCustomerId(Long customerId);


}