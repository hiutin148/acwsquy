package com.hiutin.awcsquy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiutin.awcsquy.dto.request.CartItemRequest;
import com.hiutin.awcsquy.dto.response.CartResponse;
import com.hiutin.awcsquy.entity.Cart;
import com.hiutin.awcsquy.entity.CartItem;
import com.hiutin.awcsquy.entity.Product;
import com.hiutin.awcsquy.entity.User;
import com.hiutin.awcsquy.exception.ResourceNotFoundException;
import com.hiutin.awcsquy.mapper.CartMapper;
import com.hiutin.awcsquy.repository.CartItemRepository;
import com.hiutin.awcsquy.repository.CartRepository;
import com.hiutin.awcsquy.repository.ProductRepository;
import com.hiutin.awcsquy.repository.UserRepository;
import com.hiutin.awcsquy.service.CartService;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartMapper cartMapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Cart getOrCreateCartForCurrentUser() {
        User currentUser = getCurrentUser();
        return cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(currentUser);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByCurrentUser() {
        Cart cart = getOrCreateCartForCurrentUser();
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(CartItemRequest cartItemRequest) {
        Cart cart = getOrCreateCartForCurrentUser();
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartItemRequest.getProductId()));

        if (product.getQuantity() < cartItemRequest.getQuantity()) {
            throw new IllegalArgumentException("Not enough product in stock. Available: " + product.getQuantity());
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemRequest.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + cartItemRequest.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(cartItemRequest.getQuantity());
            cart.getItems().add(newItem); // Ensure the item is added to the cart's list
            cartItemRepository.save(newItem);
        }
        // cartRepository.save(cart); // Not strictly necessary if cascade is set up or items are saved
        return cartMapper.toCartResponse(cartRepository.findById(cart.getId()).get()); // Re-fetch to get updated totals
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long cartItemId, int quantity) {
        Cart cart = getOrCreateCartForCurrentUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("User does not own this cart item");
        }
        if (quantity <= 0) {
            cart.getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            if (cartItem.getProduct().getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough product in stock. Available: " + cartItem.getProduct().getQuantity());
            }
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
        return cartMapper.toCartResponse(cartRepository.findById(cart.getId()).get());
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long cartItemId) {
        Cart cart = getOrCreateCartForCurrentUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
             throw new SecurityException("User does not own this cart item");
        }

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        // cartRepository.save(cart);
        return cartMapper.toCartResponse(cartRepository.findById(cart.getId()).get());
    }

    @Override
    @Transactional
    public void clearCart() {
        Cart cart = getOrCreateCartForCurrentUser();
        cart.getItems().clear();
        cartItemRepository.deleteAllByCartId(cart.getId()); // More efficient way
        // cartRepository.save(cart);
    }
}