package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.order.CheckoutRequest;
import com.compass.e_commerce_challenge.dto.order.OrderResponse;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.entity.OrderStatus;

public interface OrderService {
	
	/*
	 * Client
	 */
	OrderResponse checkout(CheckoutRequest request);
    PagedResponse<OrderResponse> listOrders(PageRequestDto pageRequest, boolean all);
    OrderResponse getOrder(Long orderId);
    
    /*
	 * Admin
	 */
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus); 
}
