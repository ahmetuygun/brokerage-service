package com.ing.brokerage.asset;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import com.ing.brokerage.auth.model.CustomUserDetails;
import com.ing.brokerage.auth.model.RoleEnum;
import com.ing.brokerage.common.ASSET;
import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.common.SecurityUtil;
import com.ing.brokerage.domain.asset.application.AssetService;
import com.ing.brokerage.domain.asset.domain.AssetRepository;
import com.ing.brokerage.domain.asset.domain.exception.AssetNotFoundException;
import com.ing.brokerage.domain.asset.domain.model.Asset;
import com.ing.brokerage.domain.asset.domain.model.AssetId;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.DepositRequest;
import com.ing.brokerage.domain.asset.infrastructure.rest.command.WithdrawRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    private CustomUserDetails mockUserDetails;

    @BeforeEach
    public void setup() {
        mockUserDetails = new CustomUserDetails();
        mockUserDetails.setUserId(1L);
        mockUserDetails.setAuthorities(List.of(new SimpleGrantedAuthority(RoleEnum.ROLE_USER.name())));

    }

    @Test
    public void testDeposit_whenAssetExists() throws DomainException {
        // Arrange
        Long customerId = 1L;
        BigDecimal depositSize = new BigDecimal("100.0");
        DepositRequest depositRequest = new DepositRequest(customerId, depositSize);

        Asset existingAsset = new Asset(
                new AssetId(1L),
                customerId,
                ASSET.TRY.name(),
                new BigDecimal("500.0"),
                new BigDecimal("500.0"),
                0L
        );

        when(assetRepository.retrieveAsset(customerId, ASSET.TRY.name())).thenReturn(Optional.of(existingAsset));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getAuthenticatedUser).thenReturn(mockUserDetails);

            // Act
            assetService.deposit(depositRequest);

            // Assert
            verify(assetRepository).retrieveAsset(customerId, ASSET.TRY.name());
            verify(assetRepository).save(existingAsset);
            assertEquals(new BigDecimal("600.0"), existingAsset.size());
        }
    }

    @Test
    public void testDeposit_whenAssetDoesNotExist() throws DomainException {
        // Arrange
        Long customerId = 1L;
        BigDecimal depositSize = new BigDecimal("100.0");
        DepositRequest depositRequest = new DepositRequest(customerId, depositSize);

        when(assetRepository.retrieveAsset(customerId, ASSET.TRY.name())).thenReturn(Optional.empty());
        when(assetRepository.generateId()).thenReturn(2L);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getAuthenticatedUser).thenReturn(mockUserDetails);

            // Act
            assetService.deposit(depositRequest);

            // Assert
            verify(assetRepository).retrieveAsset(customerId, ASSET.TRY.name());
            verify(assetRepository).generateId();
            verify(assetRepository).save(any(Asset.class));
        }
    }

    @Test
    public void testWithdraw_whenAssetExists() throws DomainException {
        // Arrange
        Long customerId = 1L;
        BigDecimal withdrawAmount = new BigDecimal("100.0");
        WithdrawRequest withdrawRequest = new WithdrawRequest(customerId, withdrawAmount);

        Asset existingAsset = new Asset(
                new AssetId(1L),
                customerId,
                ASSET.TRY.name(),
                new BigDecimal("500.0"), // Initial size
                new BigDecimal("500.0"), // Other values
                0L
        );

        // Mock the behavior of the assetRepository
        when(assetRepository.retrieveAsset(customerId, ASSET.TRY.name())).thenReturn(Optional.of(existingAsset));

        // Mock SecurityUtil
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getAuthenticatedUser).thenReturn(mockUserDetails);

            // Act
            assetService.withdraw(withdrawRequest);

            // Assert
            verify(assetRepository, times(1)).retrieveAsset(customerId, ASSET.TRY.name());
            verify(assetRepository, times(1)).save(existingAsset);
            assertEquals(new BigDecimal("400.0"), existingAsset.usableSize());
        }
    }

    @Test
    public void testWithdraw_whenAssetDoesNotExist() {
        // Arrange
        Long customerId = 1L;
        BigDecimal withdrawAmount = new BigDecimal("100.0");
        WithdrawRequest withdrawRequest = new WithdrawRequest(customerId, withdrawAmount);

        // Mock the behavior of the assetRepository
        when(assetRepository.retrieveAsset(customerId, ASSET.TRY.name())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(AssetNotFoundException.class, () -> {
            assetService.withdraw(withdrawRequest);
        });

        String expectedMessage = "Asset not found for customer ID: " + customerId + " and asset name: " + ASSET.TRY.name();
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(assetRepository, times(1)).retrieveAsset(customerId, ASSET.TRY.name());
        verify(assetRepository, times(0)).save(any(Asset.class)); // Ensure save is not called
    }


}
