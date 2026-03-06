package com.haiilo.interview.haiilosupermarketcheckout.domain.service;

import com.haiilo.interview.haiilosupermarketcheckout.api.dto.CheckoutItemRequestDTO;
import com.haiilo.interview.haiilosupermarketcheckout.api.dto.CustomerDTO;
import com.haiilo.interview.haiilosupermarketcheckout.api.dto.OrderRequestDTO;
import com.haiilo.interview.haiilosupermarketcheckout.domain.model.Customer;
import com.haiilo.interview.haiilosupermarketcheckout.domain.model.Order;
import com.haiilo.interview.haiilosupermarketcheckout.domain.model.OrderItem;
import com.haiilo.interview.haiilosupermarketcheckout.domain.model.Product;
import com.haiilo.interview.haiilosupermarketcheckout.domain.model.WeeklyOffer;
import com.haiilo.interview.haiilosupermarketcheckout.infrastructure.persistence.OrderRepository;
import com.haiilo.interview.haiilosupermarketcheckout.infrastructure.persistence.ProductRepository;
import com.haiilo.interview.haiilosupermarketcheckout.infrastructure.persistence.WeeklyOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final WeeklyOfferRepository weeklyOfferRepository;

    @Override
    @Transactional
    public Order placeOrder(OrderRequestDTO orderRequest) {
        log.info("Processing new order request with {} items", orderRequest.getItems().size());

        Map<UUID, Product> productMap = getProductMap(orderRequest.getItems());
        List<WeeklyOffer> activeOffers = weeklyOfferRepository.findAll();

        Order order = new Order();
        
        Customer customer = mapCustomer(orderRequest.getCustomer());
        order.setCustomer(customer);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CheckoutItemRequestDTO itemDto : orderRequest.getItems()) {
            Product product = productMap.get(itemDto.getProductId());
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid product ID: " + itemDto.getProductId());
            }
            orderItems.add(createOrderItem(itemDto, product, activeOffers));
        }

        calculateAndSetOrderTotals(order, orderItems);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        log.info("Order successfully saved with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    private Customer mapCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setStreet(dto.getStreet());
        customer.setCity(dto.getCity());
        customer.setCountry(dto.getCountry());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail());
        return customer;
    }

    private Map<UUID, Product> getProductMap(List<CheckoutItemRequestDTO> checkoutItems) {
        List<UUID> productIds = checkoutItems.stream()
                .map(CheckoutItemRequestDTO::getProductId)
                .collect(Collectors.toList());
        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    private OrderItem createOrderItem(CheckoutItemRequestDTO itemDto, Product product, List<WeeklyOffer> activeOffers) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setOriginalPrice(product.getPrice());
        orderItem.setTotalOriginalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));

        applyOffer(orderItem, activeOffers);

        return orderItem;
    }

    private void applyOffer(OrderItem orderItem, List<WeeklyOffer> activeOffers) {
        for (WeeklyOffer offer : activeOffers) {
            if (offer.getProduct().getId().equals(orderItem.getProductId()) && orderItem.getQuantity() >= offer.getRequiredQuantity()) {
                BigDecimal priceForOfferBundle = offer.getOfferPrice();
                int remainingQty = orderItem.getQuantity();
                int offerCount = 0;
                while (remainingQty >= offer.getRequiredQuantity()) {
                    remainingQty = remainingQty - offer.getRequiredQuantity();
                    offerCount++;
                }
                BigDecimal remainingPrice = orderItem.getOriginalPrice().multiply(BigDecimal.valueOf(remainingQty));
                BigDecimal finalPrice = priceForOfferBundle.multiply(BigDecimal.valueOf(offerCount)).add(remainingPrice);
                System.out.println(finalPrice);
                orderItem.setFinalPrice(finalPrice);
                orderItem.setDiscountAmount(orderItem.getTotalOriginalPrice().subtract(finalPrice));
                orderItem.setAppliedOffer(offer);
                return;
            }
        }
        orderItem.setFinalPrice(orderItem.getTotalOriginalPrice());
    }

    private void calculateAndSetOrderTotals(Order order, List<OrderItem> orderItems) {
        BigDecimal totalOriginal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal finalTotal = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {
            totalOriginal = totalOriginal.add(item.getTotalOriginalPrice());
            totalDiscount = totalDiscount.add(item.getDiscountAmount());
            finalTotal = finalTotal.add(item.getFinalPrice());
        }

        order.setTotalOriginalPrice(totalOriginal);
        order.setTotalDiscountAmount(totalDiscount);
        order.setFinalTotalPrice(finalTotal);
    }
}
