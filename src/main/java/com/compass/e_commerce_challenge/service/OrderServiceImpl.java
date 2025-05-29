package com.compass.e_commerce_challenge.service;

import java.util.ArrayList;
import java.util.List;
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
import com.compass.e_commerce_challenge.repository.CartItemRepository;
import com.compass.e_commerce_challenge.repository.CartRepository;
import com.compass.e_commerce_challenge.repository.OrderRepository;
import com.compass.e_commerce_challenge.repository.ProductRepository;
import com.compass.e_commerce_challenge.repository.UserRepository;
import com.compass.e_commerce_challenge.util.exceptions.EmptyCartException;
import com.compass.e_commerce_challenge.util.exceptions.InsufficientStockException;
import com.compass.e_commerce_challenge.util.exceptions.OperationNotAllowedException;
import com.compass.e_commerce_challenge.util.exceptions.ProductInactiveException;
import com.compass.e_commerce_challenge.util.exceptions.ResourceNotFoundException;
import com.compass.e_commerce_challenge.util.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User's cart", "user ID", userId));

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot checkout with an empty cart.");
        }

        User user = cart.getUser(); 
        Order order = Order.builder()
            .user(user)
            .status(OrderStatus.PENDING)
            .items(new ArrayList<>())
            .build();

        for (CartItem ci : new ArrayList<>(cart.getItems())) {
            Product product = ci.getProduct();

            if (!product.getActive()){
                throw new ProductInactiveException("Product '" + product.getName() + "' is inactive and cannot be purchased.");
            }
            if (product.getStockQuantity() < ci.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + ". Requested: " + ci.getQuantity() + ", Available: " + product.getStockQuantity());
            }

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
        Order savedOrder = orderRepository.save(order);
        
        List<CartItem> itemsToRemove = new ArrayList<>(cart.getItems());
        for (CartItem item : itemsToRemove) {
            cart.getItems().remove(item); 
            cartItemRepository.delete(item); 
        }

        if (cart.getItems().isEmpty()) 
             cartRepository.save(cart);
       
        return toResponse(savedOrder);
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
        if (all) {
            if (!hasAdminRole()) {
                 throw new OperationNotAllowedException("Only administrators can list all orders.");
            }
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
                .orElseThrow(() -> new ResourceNotFoundException("Order", "ID", orderId));

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELED) {
            throw new OperationNotAllowedException("The order is already " + currentStatus.toString().toLowerCase() + " and cannot be updated.");
        }
        
        if (currentStatus == newStatus)
            return toResponse(order);
        
        
        if (newStatus == OrderStatus.CANCELED) {
            if (currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.SHIPPED) { 
                 restockProductsForCanceledOrder(order);
            } else {
                 throw new OperationNotAllowedException("Cannot cancel an order that is already " + currentStatus.toString().toLowerCase());
            }
        }
        
        if (currentStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.PENDING) {
            throw new OperationNotAllowedException("Cannot revert a shipped order to pending.");
        }
         if (currentStatus == OrderStatus.DELIVERED && (newStatus == OrderStatus.PENDING || newStatus == OrderStatus.SHIPPED)) {
            throw new OperationNotAllowedException("A delivered order cannot be changed to pending or shipped.");
        }

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
            .orElseThrow(() -> new ResourceNotFoundException("Order", "ID", orderId));
        Long currentUserId = SecurityUtils.getCurrentUserId(userRepository);
        
        if (!hasAdminRole() && !order.getUser().getId().equals(currentUserId)) {
            throw new OperationNotAllowedException("Access denied. You do not have permission to view this order.");
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