package com.hiutin.awcsquy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    // Mặc định sẽ lấy địa chỉ của user, nhưng có thể override ở đây
    private String shippingAddress;
    // Các thông tin khác cho đơn hàng nếu cần
}