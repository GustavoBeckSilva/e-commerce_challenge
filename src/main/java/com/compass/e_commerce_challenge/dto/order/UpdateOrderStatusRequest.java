package com.compass.e_commerce_challenge.dto.order;

import com.compass.e_commerce_challenge.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "New status cannot be null")
    private OrderStatus newStatus;
}
