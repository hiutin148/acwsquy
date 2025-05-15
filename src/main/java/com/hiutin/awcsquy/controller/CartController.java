package com.hiutin.awcsquy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hiutin.awcsquy.dto.request.CartItemRequest;
import com.hiutin.awcsquy.dto.response.CartResponse;
import com.hiutin.awcsquy.dto.response.MessageResponse;
import com.hiutin.awcsquy.service.CartService;

@Tag(name = "Cart Management", description = "APIs for managing user shopping cart")
@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('BUYER')")
@SecurityRequirement(name = "bearerAuth")
@Validated // For path variable validation
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "Get current user's cart")
    @GetMapping
    public ResponseEntity<CartResponse> getCurrentUserCart() {
        return ResponseEntity.ok(cartService.getCartByCurrentUser());
    }

    @Operation(summary = "Add an item to the cart")
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.ok(cartService.addItemToCart(cartItemRequest));
    }

    @Operation(summary = "Update quantity of an item in the cart")
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam @Min(0) int quantity) { // quantity = 0 means remove
        return ResponseEntity.ok(cartService.updateCartItem(cartItemId, quantity));
    }

    @Operation(summary = "Remove an item from the cart")
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(cartItemId));
    }

    @Operation(summary = "Clear all items from the cart")
    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok(new MessageResponse("Cart cleared successfully."));
    }
}