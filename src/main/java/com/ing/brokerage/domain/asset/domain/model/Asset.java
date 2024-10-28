package com.ing.brokerage.domain.asset.domain.model;


import com.ing.brokerage.auth.model.CustomUserDetails;
import com.ing.brokerage.auth.model.RoleEnum;
import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.common.SecurityUtil;
import com.ing.brokerage.domain.asset.domain.exception.InsufficientBalanceException;
import com.ing.brokerage.domain.asset.domain.exception.UnauthorizedAccessException;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.DepositRequest;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.WithdrawRequest;
import com.ing.brokerage.common.AggregateRoot;
import com.ing.brokerage.domain.order.domain.info.OrderSide;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.util.List;


public class Asset extends AggregateRoot<AssetId> {

    protected Asset(AssetId id) {
        super(id);
    }

    public Asset(AssetId id, Long customerId, String assetName, BigDecimal size, BigDecimal usableSize,Long version) {
        super(id);
        this.customerId = customerId;
        this.assetName = assetName;
        this.size = size;
        this.usableSize = usableSize;
        updateVersion(version);
    }

    private Long customerId;
    private String assetName;
    private BigDecimal size;
    private BigDecimal usableSize;
    public Long customerId() {
        return customerId;
    }

    public String assetName() {
        return assetName;
    }

    public BigDecimal size() {
        return size;
    }

    public BigDecimal usableSize() {
        return usableSize;
    }

    public void validateSufficientFundsOrShares(Asset assetTry, OrderSide orderSide, BigDecimal orderSize, BigDecimal price) throws InsufficientBalanceException, UnauthorizedAccessException {

        if (OrderSide.BUY.equals(orderSide)) {
            BigDecimal totalCost = price.multiply(orderSize);
            if (assetTry.usableSize.compareTo(totalCost) < 0) {
                throw new InsufficientBalanceException("Insufficient TRY balance for buy order");
            }
        } else if ((OrderSide.SELL.equals(orderSide))) {
            if (usableSize.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InsufficientBalanceException("Insufficient quantity to complete the sell order. Current size: " + this.usableSize + ", Order size: " + orderSize);
            }
            if (usableSize.compareTo(orderSize) < 0) {
                throw new InsufficientBalanceException("Insufficient asset quantity for sell order");
            }
        } else {
            throw new IllegalArgumentException("Invalid order side");
        }
    }

    public void deductForOrderReservation(OrderSide orderSide, BigDecimal orderSize) {
        if (orderSide == OrderSide.BUY) {
            this.size = this.size.add(orderSize);
        } else if (orderSide == OrderSide.SELL) {
            this.size = this.size.subtract(orderSize);
        }
    }

    public void restoreReservedFundsOrShares(OrderSide orderSide, BigDecimal orderSize, BigDecimal price) {

        if (orderSide == OrderSide.BUY) {
            this.size = this.size.subtract(orderSize);
        } else if (orderSide == OrderSide.SELL) {
            this.size = this.size.add(orderSize);
        }
    }

    public void validateForDeposit(DepositRequest depositRequest) throws DomainException {

        CustomUserDetails userDetail = SecurityUtil.getAuthenticatedUser();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDetail.getAuthorities();
        boolean isUserRole = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(RoleEnum.ROLE_USER.name()));
        if (isUserRole) {
            if (!depositRequest.getCustomerId().equals(userDetail.getUserId())) {
                throw new UnauthorizedAccessException("Customer ID does not match the authenticated user's ID.");
            }
        }

        if (depositRequest.getSize() == null || !(depositRequest.getSize().compareTo(BigDecimal.ZERO) > 0)) {
            throw new IllegalArgumentException("Deposit size must be positive");
        }

    }

    public void validateForWithdraw(WithdrawRequest withdrawRequest) throws InsufficientBalanceException, UnauthorizedAccessException {


        CustomUserDetails userDetail = SecurityUtil.getAuthenticatedUser();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDetail.getAuthorities();
        boolean isUserRole = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(RoleEnum.ROLE_USER.name()));
        if (isUserRole) {
            if (!withdrawRequest.getCustomerId().equals(userDetail.getUserId())) {
                throw new UnauthorizedAccessException("Customer ID does not match the authenticated user's ID.");
            }
        }

        if (withdrawRequest.getSize().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientBalanceException("Withdrawal amount must be greater than zero.");
        }

        // Check if sufficient balance is available for withdrawal
        if (withdrawRequest.getSize().compareTo(usableSize) > 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal.");
        }



    }

    public void deposit(BigDecimal depositSize) {
        size = size.add(depositSize);
    }

    public void withdraw(BigDecimal withDrawSize) {

        usableSize = usableSize.subtract(withDrawSize);

    }

    public void updateSufficientFundsOrShares(OrderSide orderSide, BigDecimal amount, BigDecimal price) {

        if (OrderSide.BUY.equals(orderSide)) {
            BigDecimal totalCost = price.multiply(amount);
            usableSize = usableSize.subtract(totalCost);
        } else if (OrderSide.SELL.equals(orderSide)) {
            BigDecimal totalCost = price.multiply(amount);
            usableSize = usableSize.add(totalCost);
        }

    }
}
