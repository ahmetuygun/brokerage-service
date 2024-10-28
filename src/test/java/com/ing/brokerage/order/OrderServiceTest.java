package com.ing.brokerage.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ing.brokerage.auth.model.CustomUserDetails;
import com.ing.brokerage.auth.model.RoleEnum;
import com.ing.brokerage.common.DomainException;
import com.ing.brokerage.common.SecurityUtil;
import com.ing.brokerage.domain.order.application.OrderService;
import com.ing.brokerage.domain.order.domain.exception.OrderNotFoundException;
import com.ing.brokerage.domain.order.domain.info.OrderSide;
import com.ing.brokerage.domain.order.domain.model.Order;
import com.ing.brokerage.domain.order.domain.repository.OrderRepository;
import com.ing.brokerage.domain.order.infrastructure.rest.command.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest orderRequest;

    private CustomUserDetails mockUserDetails;


    @BeforeEach
    public void setup() {
        orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        orderRequest.setAssetName("Gold");
        orderRequest.setOrderSide(OrderSide.BUY);
        orderRequest.setSize(BigDecimal.valueOf(10));
        orderRequest.setPrice(BigDecimal.valueOf(1000));


        mockUserDetails = new CustomUserDetails();
        mockUserDetails.setUserId(1L);
        mockUserDetails.setAuthorities(List.of(new SimpleGrantedAuthority(RoleEnum.ROLE_USER.name())));
    }

    @Test
    public void testProcessOrder() throws DomainException {
        // Arrange
        Long customerId = 1L;
        OrderRequest orderRequest = new OrderRequest(customerId, "TRY", OrderSide.BUY, new BigDecimal("100.0"), new BigDecimal("10.0"));

        when(orderRepository.generateId()).thenReturn(1L);

        // Mocking the authenticated user details
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getAuthenticatedUser).thenReturn(mockUserDetails);

            // Act
            orderService.processOrder(orderRequest);

            // Assert
            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(orderCaptor.capture());

            Order savedOrder = orderCaptor.getValue();
            assertEquals(orderRequest.getCustomerId(), savedOrder.customerId());
            assertEquals(orderRequest.getAssetName(), savedOrder.assetName());
            assertEquals(orderRequest.getOrderSide(), savedOrder.orderSide());
            assertEquals(orderRequest.getSize(), savedOrder.size());
            assertEquals(orderRequest.getPrice(), savedOrder.price());
            verify(applicationEventPublisher, times(1)).publishEvent(any());
        }
    }

    @Test
    public void testCancelOrder_whenOrderExists() throws DomainException {
        Order order = mock(Order.class);
        when(orderRepository.retrieveOrder(1L)).thenReturn(Optional.of(order));

        doNothing().when(order).validateForCancellation();
        doNothing().when(order).cancel();

        // Act
        orderService.cancelOrder(1L);

        // Assert
        verify(order).validateForCancellation();
        verify(order).cancel();
        verify(orderRepository).save(order);

    }

    @Test
    public void testCancelOrder_whenOrderDoesNotExist() {
        // Arrange
        when(orderRepository.retrieveOrder(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.cancelOrder(1L);
        });

        String expectedMessage = "Order not found for ID: 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(orderRepository, times(1)).retrieveOrder(1L);
        verify(orderRepository, times(0)).save(any(Order.class)); // Ensure save is not called
    }


}
