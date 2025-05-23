package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.product.ProductRequest;
import com.compass.e_commerce_challenge.dto.product.ProductResponse;
import com.compass.e_commerce_challenge.dto.shared.ApiResponse;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest dto);
    ProductResponse updateProduct(Long productId, ProductRequest dto);
    void deactivateProduct(Long productId);
    ApiResponse<?> deleteProduct(Long productId);
    PagedResponse<ProductResponse> listActiveProducts(PageRequestDto pageRequest);
    PagedResponse<ProductResponse> listAllProducts(PageRequestDto pageRequest);
    ProductResponse getProductById(Long productId);
}
