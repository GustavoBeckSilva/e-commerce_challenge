package com.compass.e_commerce_challenge.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compass.e_commerce_challenge.dto.cart.CartItemResponse;
import com.compass.e_commerce_challenge.dto.cart.CartResponse;
import com.compass.e_commerce_challenge.dto.category.CategoryResponse;
import com.compass.e_commerce_challenge.dto.category.CategorySummary;
import com.compass.e_commerce_challenge.dto.order.OrderItemResponse;
import com.compass.e_commerce_challenge.dto.order.OrderResponse;
import com.compass.e_commerce_challenge.dto.product.ProductResponse;
import com.compass.e_commerce_challenge.dto.user.UpdateUserRequest;
import com.compass.e_commerce_challenge.dto.user.UserResponse;
import com.compass.e_commerce_challenge.entity.Cart;
import com.compass.e_commerce_challenge.entity.CartItem;
import com.compass.e_commerce_challenge.entity.Category;
import com.compass.e_commerce_challenge.entity.Order;
import com.compass.e_commerce_challenge.entity.OrderItem;
import com.compass.e_commerce_challenge.entity.OrderStatus;
import com.compass.e_commerce_challenge.entity.Product;
import com.compass.e_commerce_challenge.entity.User;
import com.compass.e_commerce_challenge.entity.UserRoles;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
              .setMatchingStrategy(MatchingStrategies.STRICT)
              .setPropertyCondition(Conditions.isNotNull())
              .setAmbiguityIgnored(true);

        mapper.createTypeMap(UserRoles.class, String.class)
              .setConverter(ctx -> ctx.getSource() == null ? null : ctx.getSource().name());
        mapper.createTypeMap(OrderStatus.class, String.class)
              .setConverter(ctx -> ctx.getSource() == null ? null : ctx.getSource().name());

        mapper.addMappings(userToUserResponseMap());
        mapper.addMappings(updateUserRequestToUserMap());
        mapper.addMappings(productToProductResponseMap());
        mapper.addMappings(cartItemToCartItemResponseMap());
        mapper.addMappings(cartToCartResponseMap());
        mapper.addMappings(orderItemToOrderItemResponseMap());
        mapper.addMappings(orderToOrderResponseMap());
        mapper.addMappings(categoryToCategoryResponseMap());

        return mapper;
    }

    // USER ******************************************************************************************

    private PropertyMap<User, UserResponse> userToUserResponseMap() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
                using(ctx -> ((Set<?>) ctx.getSource()).stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet()))
                  .map(source.getRoles(), destination.getRoles());
            }
        };
    }

    private PropertyMap<UpdateUserRequest, User> updateUserRequestToUserMap() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
                map().setUsername(source.getUsername());
                map().setEmail(source.getEmail());
                map().setAddress(source.getAddress());
                map().setActive(source.getActive());
            }
        };
    }

    // PRODUCT & CATEGORY ******************************************************************************************

    private PropertyMap<Product, ProductResponse> productToProductResponseMap() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
                using(categorySetToSummaryListConverter())
                    .map(source.getCategories(), destination.getCategories());
            }
        };
    }

    private Converter<Set<Category>, List<CategorySummary>> categorySetToSummaryListConverter() {
        return ctx -> {
            Set<Category> categories = ctx.getSource();
            if (categories == null) return List.of();

            return categories.stream()
                .map(cat -> CategorySummary.builder()
                                           .id(cat.getId())
                                           .name(cat.getName())
                                           .build())
                .collect(Collectors.toList());
        };
    }

    private PropertyMap<Category, CategoryResponse> categoryToCategoryResponseMap() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
            }
        };
    }

    // CART ******************************************************************************************

    private PropertyMap<CartItem, CartItemResponse> cartItemToCartItemResponseMap() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
                map().setProductId(source.getProduct().getId());
                map().setProductName(source.getProduct().getName());
                map().setUnitPrice(source.getProduct().getPrice());
            }
        };
    }

    private PropertyMap<Cart, CartResponse> cartToCartResponseMap() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
                // Campos mapeados automaticamente
            }
        };
    }

    // ORDER ******************************************************************************************

    private PropertyMap<OrderItem, OrderItemResponse> orderItemToOrderItemResponseMap() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
                map().setProductId(source.getProduct().getId());
                map().setProductName(source.getProduct().getName());
                map().setUnitPrice(source.getUnitPrice());
                map().setQuantity(source.getQuantity());
            }
        };
    }

    private PropertyMap<Order, OrderResponse> orderToOrderResponseMap() {
        return new PropertyMap<>() {
            @Override
            protected void configure() {
                map(source.getStatus(), destination.getStatus());
            }
        };
    }
}

