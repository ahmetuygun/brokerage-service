package com.ing.brokerage.domain.asset.infrastructure.rest;

import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.domain.asset.application.AssetService;
import com.ing.brokerage.domain.asset.domain.exception.AssetNotFoundException;
import com.ing.brokerage.domain.asset.domain.exception.InsufficientBalanceException;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.AssetDto;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.DepositRequest;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.WithdrawRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestBody DepositRequest depositRequest) throws DomainException {
        assetService.deposit(depositRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody WithdrawRequest withdrawRequest) throws DomainException {
        assetService.withdraw(withdrawRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<AssetDto>> listAssetsByCustomerId(@PathVariable Long customerId) {
        List<AssetDto> assets = assetService.listAsset(customerId);
        return ResponseEntity.ok(assets);
    }
}

