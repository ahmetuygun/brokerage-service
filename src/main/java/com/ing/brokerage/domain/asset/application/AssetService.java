package com.ing.brokerage.domain.asset.application;

import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.domain.asset.domain.AssetRepository;
import com.ing.brokerage.domain.asset.domain.exception.AssetNotFoundException;
import com.ing.brokerage.domain.asset.domain.model.Asset;
import com.ing.brokerage.domain.asset.domain.model.AssetId;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.AssetDto;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.DepositRequest;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.WithdrawRequest;
import com.ing.brokerage.common.ASSET;
import com.ing.brokerage.domain.order.application.event.OrderCanceledEvent;
import com.ing.brokerage.domain.order.application.event.OrderCreatedEvent;
import com.ing.brokerage.domain.order.domain.info.OrderSide;
import com.ing.brokerage.domain.order.domain.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AssetService {
    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
    public void validateAndReserveForOrder(OrderCreatedEvent orderCreatedEvent) throws DomainException {

        Asset mainAsset = assetRepository.retrieveAsset(
                orderCreatedEvent.getCustomerId(),
                ASSET.TRY.name()
        ).orElseThrow(() -> new AssetNotFoundException(
                "Asset not found for customer ID: " + orderCreatedEvent.getCustomerId() +
                        " and asset name: " +  ASSET.TRY.name()
        ));

        Asset asset = assetRepository.retrieveAsset(
                orderCreatedEvent.getCustomerId(),
                orderCreatedEvent.getAssetName()
        ).orElseGet(() -> {
            Long newId = assetRepository.generateId();
            return new Asset(new AssetId(newId),
                    orderCreatedEvent.getCustomerId(),
                    orderCreatedEvent.getAssetName(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO, 0L);
        });
        asset.validateSufficientFundsOrShares(mainAsset, orderCreatedEvent.getOrderSide(),orderCreatedEvent.getSize(), orderCreatedEvent.getPrice());
        asset.deductForOrderReservation(orderCreatedEvent.getOrderSide(),orderCreatedEvent.getSize());
        mainAsset.updateSufficientFundsOrShares(orderCreatedEvent.getOrderSide(),orderCreatedEvent.getSize(), orderCreatedEvent.getPrice());
        assetRepository.save(asset);
        assetRepository.save(mainAsset);

    }

    public void handleOrderCancellation(OrderCanceledEvent orderCanceledEvent) throws AssetNotFoundException {


        Asset mainAsset = assetRepository.retrieveAsset(
                orderCanceledEvent.getCustomerId(),
                ASSET.TRY.name()
        ).orElseThrow(() -> new AssetNotFoundException(
                "Asset not found for customer ID: " + orderCanceledEvent.getCustomerId() +
                        " and asset name: " +  ASSET.TRY.name()
        ));

        Asset asset = assetRepository.retrieveAsset(
                orderCanceledEvent.getCustomerId(),
                orderCanceledEvent.getAssetName()
        ).orElseThrow(() -> new AssetNotFoundException(
                "Asset not found for customer ID: " + orderCanceledEvent.getCustomerId() +
                        " and asset name: " + orderCanceledEvent.getAssetName()
        ));

        Order order = orderCanceledEvent.getOrder();
        // Determine the opposing order side
        OrderSide opposingOrderSide = OrderSide.SELL.equals(order.orderSide()) ? OrderSide.BUY : OrderSide.SELL;
        asset.deductForOrderReservation(opposingOrderSide, order.size());
        mainAsset.updateSufficientFundsOrShares(opposingOrderSide, order.size(), order.price());

        assetRepository.save(asset);
        assetRepository.save(mainAsset);


    }

    public void deposit(DepositRequest depositRequest) throws DomainException {
        Asset asset = assetRepository.retrieveAsset(
                depositRequest.getCustomerId(),
                ASSET.TRY.name()
        ).orElseGet(() -> {
            Long newId = assetRepository.generateId();
            return new Asset(new AssetId(newId),
                    depositRequest.getCustomerId(),
                    ASSET.TRY.name(),
                    BigDecimal.ZERO,
                   BigDecimal.ZERO, 0L);
        });
        asset.validateForDeposit(depositRequest);
        asset.deposit(depositRequest.getSize());
        assetRepository.save(asset);
    }

    public void withdraw(WithdrawRequest withdrawRequest) throws DomainException {

        Asset asset = assetRepository.retrieveAsset(
                withdrawRequest.getCustomerId(),
                ASSET.TRY.name()
        ).orElseThrow(() -> new AssetNotFoundException(
                "Asset not found for customer ID: " + withdrawRequest.getCustomerId() +
                        " and asset name: " +  ASSET.TRY.name()
        ));

        asset.validateForWithdraw(withdrawRequest);
        asset.withdraw(withdrawRequest.getSize());
        assetRepository.save(asset);

    }

    public List<AssetDto> listAsset(Long customerId) {

        return assetRepository.listAssetByCustomer(customerId);
    }
}
