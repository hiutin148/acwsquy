package com.hiutin.awcsquy.service;

import com.hiutin.awcsquy.dto.request.CartItemRequest;
import com.hiutin.awcsquy.dto.response.CartResponse;

public interface CartService {
    CartResponse getCartByCurrentUser();
    CartResponse addItemToCart(CartItemRequest cartItemRequest);
    CartResponse updateCartItem(Long cartItemId, int quantity);
    CartResponse removeItemFromCart(Long cartItemId);
    void clearCart();
}