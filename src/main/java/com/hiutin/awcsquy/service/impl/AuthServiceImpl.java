package com.hiutin.awcsquy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiutin.awcsquy.dto.request.LoginRequest;
import com.hiutin.awcsquy.dto.request.RegisterRequest;
import com.hiutin.awcsquy.dto.response.AuthResponse;
import com.hiutin.awcsquy.dto.response.UserResponse;
import com.hiutin.awcsquy.entity.Cart;
import com.hiutin.awcsquy.entity.User;
import com.hiutin.awcsquy.entity.enums.Role;
import com.hiutin.awcsquy.mapper.UserMapper;
import com.hiutin.awcsquy.repository.CartRepository;
import com.hiutin.awcsquy.repository.UserRepository;
import com.hiutin.awcsquy.security.jwt.JwtTokenProvider;
import com.hiutin.awcsquy.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository; // Inject CartRepository
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        User user = userMapper.registerRequestToUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(user);

        // Create a cart for BUYERs and SELLERs upon registration
        if (savedUser.getRole() == Role.BUYER || savedUser.getRole() == Role.SELLER) {
            Cart cart = new Cart();
            cart.setUser(savedUser);
            cartRepository.save(cart);
            // savedUser.setCart(cart); // JPA will handle this relationship if bidirectional
        }
    }

    @Override
    public AuthResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        UserResponse userResponse = userMapper.toUserResponse(userDetails);

        return new AuthResponse(jwt, userResponse);
    }
}