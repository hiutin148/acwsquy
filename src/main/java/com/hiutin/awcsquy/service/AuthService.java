package com.hiutin.awcsquy.service;

import com.hiutin.awcsquy.dto.request.LoginRequest;
import com.hiutin.awcsquy.dto.request.RegisterRequest;
import com.hiutin.awcsquy.dto.response.AuthResponse;

public interface AuthService {
    void registerUser(RegisterRequest registerRequest);
    AuthResponse loginUser(LoginRequest loginRequest);
}