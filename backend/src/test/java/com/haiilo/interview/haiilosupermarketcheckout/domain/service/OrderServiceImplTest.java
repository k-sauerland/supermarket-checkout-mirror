package com.haiilo.interview.haiilosupermarketcheckout.domain.service;

import com.haiilo.interview.haiilosupermarketcheckout.api.dto.CheckoutItemRequestDTO;
import com.haiilo.interview.haiilosupermarketcheckout.api.dto.CustomerDTO;
import com.haiilo.interview.haiilosupermarketcheckout.api.dto.OrderRequestDTO;
import com.haiilo.interview.haiilosupermarketcheckout.domain.model.Order;
import com.haiilo.interview.haiilosupermarketcheckout.domain.model.Product;
import com.haiilo.interview.haiilosupermarketcheckout.domain.model.WeeklyOffer;
import com.haiilo.interview.haiilosupermarketcheckout.infrastructure.persistence.OrderRepository;
import com.haiilo.interview.haiilosupermarketcheckout.infrastructure.persistence.ProductRepository;
import com.haiilo.interview.haiilosupermarketcheckout.infrastructure.persistence.WeeklyOfferRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private WeeklyOfferRepository weeklyOfferRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Should calculate totals and save order correctly when placing an order")
    void shouldPlaceOrderAndCalculateTotals() {
        CheckoutItemRequestDTO checkoutItem = new CheckoutItemRequestDTO();
        UUID productId = UUID.randomUUID();
        checkoutItem.setProductId(productId);
        checkoutItem.setQuantity(3);
        List<CheckoutItemRequestDTO> checkoutItems = Collections.singletonList(checkoutItem);

        CustomerDTO customer = new CustomerDTO();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setStreet("123 Main St");
        customer.setCity("New York");
        customer.setCountry("USA");
        customer.setPhoneNumber("+1 555 1234");
        customer.setEmail("john.doe@example.com");

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setItems(checkoutItems);
        orderRequest.setCustomer(customer);

        Product product = new Product();
        product.setName("Apple");
        product.setPrice(new BigDecimal("0.90"));
        product.setId(productId);

        when(productRepository.findAllById(any(List.class))).thenReturn(Collections.singletonList(product));
        when(weeklyOfferRepository.findAll()).thenReturn(Collections.emptyList()); // No offers in this test
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the same order that was passed to save

        Order result = orderService.placeOrder(orderRequest);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("2.70"), result.getTotalOriginalPrice());
        assertEquals(BigDecimal.ZERO, result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("2.70"), result.getFinalTotalPrice());
        assertNotNull(result.getCustomer());
        assertEquals("John", result.getCustomer().getFirstName());

        verify(productRepository, times(1)).findAllById(any(List.class));
        verify(weeklyOfferRepository, times(1)).findAll();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should apply weekly offer correctly when placing an order")
    void shouldApplyOfferWhenPlacingOrder() {
        CheckoutItemRequestDTO checkoutItem = new CheckoutItemRequestDTO();
        UUID productId = UUID.randomUUID();
        checkoutItem.setProductId(productId);
        checkoutItem.setQuantity(3); // 3 items, offer is for 2
        List<CheckoutItemRequestDTO> checkoutItems = Collections.singletonList(checkoutItem);

        CustomerDTO customer = new CustomerDTO();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setStreet("123 Main St");
        customer.setCity("New York");
        customer.setCountry("USA");
        customer.setPhoneNumber("+1 555 1234");
        customer.setEmail("john.doe@example.com");

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setItems(checkoutItems);
        orderRequest.setCustomer(customer);

        Product product = new Product();
        product.setName("Apple");
        product.setPrice(new BigDecimal("0.90"));
        product.setId(productId);

        WeeklyOffer offer = new WeeklyOffer();
        offer.setProduct(product);
        offer.setRequiredQuantity(2);
        offer.setOfferPrice(new BigDecimal("1.00"));

        when(productRepository.findAllById(any(List.class))).thenReturn(Collections.singletonList(product));
        when(weeklyOfferRepository.findAll()).thenReturn(Collections.singletonList(offer));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.placeOrder(orderRequest);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("2.70"), result.getTotalOriginalPrice());
        assertEquals(new BigDecimal("1.90"), result.getFinalTotalPrice());
        assertEquals(new BigDecimal("0.80"), result.getTotalDiscountAmount());
        assertNotNull(result.getCustomer());

        // Test Breaker
        assertEquals(1,2)

        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
