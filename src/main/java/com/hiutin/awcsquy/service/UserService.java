package com.hiutin.awcsquy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hiutin.awcsquy.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> getAllUsers(Pageable pageable); // For Admin
    // updateUserDetails, deleteUser (for Admin)
}