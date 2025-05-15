package com.hiutin.awcsquy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiutin.awcsquy.dto.request.OrderRequest;
import com.hiutin.awcsquy.dto.response.OrderResponse;
import com.hiutin.awcsquy.entity.Cart;
import com.hiutin.awcsquy.entity.CartItem;
import com.hiutin.awcsquy.entity.Order;
import com.hiutin.awcsquy.entity.OrderItem;
import com.hiutin.awcsquy.entity.Product;
import com.hiutin.awcsquy.entity.User;
import com.hiutin.awcsquy.entity.enums.OrderStatus;
import com.hiutin.awcsquy.exception.ResourceNotFoundException;
import com.hiutin.awcsquy.mapper.OrderMapper;
import com.hiutin.awcsquy.repository.CartItemRepository;
import com.hiutin.awcsquy.repository.CartRepository;
import com.hiutin.awcsquy.repository.OrderRepository;
import com.hiutin.awcsquy.repository.ProductRepository;
import com.hiutin.awcsquy.repository.UserRepository;
import com.hiutin.awcsquy.service.EmailService;
import com.hiutin.awcsquy.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    @Qualifier("mockEmailService") // Use qualifier for mock service
    private EmailService emailService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public OrderResponse createOrderFromCart(OrderRequest orderRequest) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for current user"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from an empty cart.");
        }

        Order order = new Order();
        order.setUser(currentUser);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setShippingAddress(orderRequest.getShippingAddress() != null ? orderRequest.getShippingAddress() : currentUser.getAddress());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName() +
                                                ". Available: " + product.getQuantity() +
                                                ", Requested: " + cartItem.getQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPricePerUnit(product.getPrice()); // Price at the time of order

            orderItems.add(orderItem);
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            // Decrease product stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
        // orderItemRepository.saveAll(orderItems); // Already cascaded by Order save

        // Clear the cart
        cart.getItems().clear();
        cartItemRepository.deleteAllByCartId(cart.getId());
        // cartRepository.save(cart); // Not needed if items are deleted

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse processPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Simulate payment success
        if (order.getStatus() == OrderStatus.CREATED) {
            order.setStatus(OrderStatus.PAID);
            Order updatedOrder = orderRepository.save(order);

            // Send mock confirmation email
            emailService.sendOrderConfirmationEmail(
                    updatedOrder.getUser().getEmail(),
                    updatedOrder.getId().toString(),
                    updatedOrder.getUser().getFullName()
            );
            return orderMapper.toOrderResponse(updatedOrder);
        } else {
            throw new IllegalStateException("Order cannot be paid. Current status: " + order.getStatus());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForCurrentUser(Long orderId) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(orderId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or does not belong to current user"));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistoryForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(currentUser.getId());
        return orders.stream().map(orderMapper::toOrderResponse).collect(Collectors.toList());
    }

    // --- Admin Methods ---
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrdersForAdmin(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toOrderResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        // Add logic here to check valid status transitions if needed
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    // --- Revenue Statistics (Simplified) ---
    @Override
    public BigDecimal getRevenueByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return orderRepository.sumTotalPriceByStatusAndOrderDateBetween(OrderStatus.COMPLETED, startOfDay, endOfDay)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getRevenueByMonth(int year, int month) {
        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.of(year, month, 1).plusMonths(1).atStartOfDay().minusNanos(1);
        return orderRepository.sumTotalPriceByStatusAndOrderDateBetween(OrderStatus.COMPLETED, startOfMonth, endOfMonth)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getRevenueByYear(int year) {
        LocalDateTime startOfYear = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime endOfYear = LocalDate.of(year, 12, 31).atTime(23, 59, 59);
        return orderRepository.sumTotalPriceByStatusAndOrderDateBetween(OrderStatus.COMPLETED, startOfYear, endOfYear)
                .orElse(BigDecimal.ZERO);
    }
}