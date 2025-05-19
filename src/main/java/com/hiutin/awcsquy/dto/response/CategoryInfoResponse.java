package com.hiutin.awcsquy.dto.response; // Hoặc package của bạn

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInfoResponse {
    private Long id;
    private String name;
    private String slug;
}