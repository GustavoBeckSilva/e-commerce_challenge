package com.compass.e_commerce_challenge.service;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compass.e_commerce_challenge.dto.order.CheckoutRequest;
import com.compass.e_commerce_challenge.dto.order.OrderItemResponse;
import com.compass.e_commerce_challenge.dto.order.OrderResponse;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.entity.Cart;
import com.compass.e_commerce_challenge.entity.CartItem;
import com.compass.e_commerce_challenge.entity.Order;
import com.compass.e_commerce_challenge.entity.OrderItem;
import com.compass.e_commerce_challenge.entity.OrderStatus;
import com.compass.e_commerce_challenge.entity.Product;
import com.compass.e_commerce_challenge.entity.User;
import com.compass.e_commerce_challenge.repository.CartRepository;
import com.compass.e_commerce_challenge.repository.OrderRepository;
import com.compass.e_commerce_challenge.repository.ProductRepository;
import com.compass.e_commerce_challenge.repository.UserRepository;
import com.compass.e_commerce_challenge.util.exceptions.BadRequestException;
import com.compass.e_commerce_challenge.util.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
	private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    
    @Override
    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new BadRequestException("Cart not found."));
        
        if (cart.getItems().isEmpty())
            throw new BadRequestException("Cart empty.");
        
        User user = cart.getUser();
        Order order = Order.builder()
            .user(user)
            .status(OrderStatus.PENDING)
            .build();

        for (CartItem ci : cart.getItems()) {
            
        	Product product = ci.getProduct();
        	
        	if(product.getStockQuantity()  < ci.getQuantity())
                throw new BadRequestException("Insufficient stock for product: " + product.getName());        	
        	
        	product.setStockQuantity(product.getStockQuantity() - ci.getQuantity());
            productRepository.save(product);
        	
        	OrderItem oi = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(ci.getQuantity())
                .unitPrice(product.getPrice())
                .build();
            order.getItems().add(oi);
        }
        
        order.setTotalAmount(order.calculateTotalAmount());
        Order saved = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> listOrders(PageRequestDto pageRequest, boolean all) {
        PageRequest pr = PageRequest.of(
            pageRequest.getPage(),
            pageRequest.getSize(),
            Sort.by(Sort.Direction.fromString(pageRequest.getDirection()), pageRequest.getSortBy())
        );
        Page<Order> page;
        if (all && hasAdminRole()) {
            page = orderRepository.findAll(pr);
        } else {
            Long userId = SecurityUtils.getCurrentUserId(userRepository);
            page = orderRepository.findByUserId(userId, pr);
        }
        return PagedResponse.<OrderResponse>builder()
            .content(page.stream().map(this::toResponse).collect(Collectors.toList()))
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
    
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELED)
            throw new BadRequestException("Order is already completed or canceled and cannot be updated.");
        
        
        if (currentStatus == newStatus)
            return toResponse(order);
        

        if (newStatus == OrderStatus.CANCELED)
            restockProductsForCanceledOrder(order);
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    private void restockProductsForCanceledOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int quantityToRestock = item.getQuantity();
            
            product.setStockQuantity(product.getStockQuantity() + quantityToRestock);
            productRepository.save(product);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BadRequestException("Order not found."));
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        if (!hasAdminRole() && !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied to the request.");
        }
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse dto = modelMapper.map(order, OrderResponse.class);
        dto.setItems(
            order.getItems().stream()
                .map(oi -> {
                    OrderItemResponse ir = modelMapper.map(oi, OrderItemResponse.class);
                    ir.setTotalPrice(oi.getSubTotal());
                    return ir;
                })
                .collect(Collectors.toList())
        );
        return dto;
    }

    private boolean hasAdminRole() {
        
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
    	if (auth == null) 
            return false;
        
        return auth.getAuthorities().stream()
                   .map(GrantedAuthority::getAuthority)
                   .anyMatch(role -> role.equals("ROLE_ADMIN"));
    
    }
}